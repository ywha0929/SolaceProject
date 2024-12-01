package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class AskBidPublisher {

    private final BlockingDeque<AskBidRequest> queuePricePublisher = new LinkedBlockingDeque<>();
    private Thread thread;
    private final String TOPIC = "askbidRequest";

    public AskBidPublisher() throws JCSMPException {

        thread = new Thread(new Runnable() {
            public void run() {
                final JCSMPProperties properties = new JCSMPProperties();
                properties.setProperty(JCSMPProperties.HOST, "tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
                properties.setProperty(JCSMPProperties.USERNAME, "SecurityCompany"+Main.CompanyName);
                properties.setProperty(JCSMPProperties.VPN_NAME, "ai6g");
                properties.setProperty(JCSMPProperties.PASSWORD, "SecurityCompany"+Main.CompanyName);

                final JCSMPSession session;
                try {
                    session = JCSMPFactory.onlyInstance().createSession(properties);
                } catch (InvalidPropertiesException e) {
                    throw new RuntimeException(e);
                }
                try {
                    System.out.println("SecurityCompany"+Main.CompanyName);
                    session.connect();
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }

                XMLMessageProducer prod = null;


                try {
                    prod = session.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
                        @Override
                        public void responseReceivedEx(Object o) {
                            System.out.println(o.toString());
                        }

                        @Override
                        public void handleErrorEx(Object o, JCSMPException cause, long timestamp) {
                            System.out.printf("### Producer handleErrorEx() callback: %s%n", cause);

                        }
                    });
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                XMLMessageConsumer consumer;
                try {
                    consumer = session.getMessageConsumer((XMLMessageListener)null);
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                try {
                    consumer.start();
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                while (true) {

                    AskBidRequest request = null;
                    try {
                        request = queuePricePublisher.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("AskBidPublisher running");

                    final Topic topic = Topic.of(String.format("%s/%s/%s/%s",TOPIC,request.stock,request.askbid,request.securityCompany));


                    TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

                    msg.setText(request.convertToJson());
                    try {
//                        prod.send(msg,topic);
                        Requestor requestor = session.createRequestor();
                        BytesXMLMessage reply = requestor.request(msg, 10000L,topic);
                        if (reply instanceof TextMessage) {
                            System.out.println(String.format("ask/bid success : %s",((TextMessage) reply).getText()));
                        }
                    } catch (JCSMPException e) {
                        System.err.println("error");
                        continue;
                    }
//                    if (request.toString().equals(Main.EXIT_MESSAGE)) {
//                        break;
//                    }
                }
            }
        });
        thread.start();
    }

    public void publishPrice(AskBidRequest message) {
        System.out.println("publishPrice");
        queuePricePublisher.add(message);
    }

}
