package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class PricePublisher {

    private final static BlockingDeque<Pair<String,String>> queuePricePublisher = new LinkedBlockingDeque<>();
    private Thread thread;
    private final String TOPIC = "SK/hi";

    public PricePublisher() throws JCSMPException {

        thread = new Thread(new Runnable() {
            public void run() {
                final JCSMPProperties properties = new JCSMPProperties();
                properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
                properties.setProperty(JCSMPProperties.USERNAME, "YWHA_KRX");
                properties.setProperty(JCSMPProperties.PASSWORD, "KRX");
                properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");

                final JCSMPSession session;
                try {
                    session = JCSMPFactory.onlyInstance().createSession(properties);
                } catch (InvalidPropertiesException e) {
                    throw new RuntimeException(e);
                }
                try {
                    session.connect();
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                XMLMessageProducer prod = null;
                try {
                    prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {

                        @Override
                        public void responseReceived(String messageID) {
                            System.out.println("Producer received response for msg: " + messageID);
                        }

                        @Override
                        public void handleError(String messageID, JCSMPException e, long timestamp) {
                            System.out.printf("Producer received error for msg: %s@%s - %s%n",
                                    messageID, timestamp, e);
                        }
                    });
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                while (true) {

                    Pair<String,String> publishTarget;
                    try {
                        publishTarget = queuePricePublisher.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("running");

                    final Topic topic = Topic.of(String.format("currentpricing/%s",publishTarget.getSecond()));
                    TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

                    msg.setText(publishTarget.getFirst());
                    try {
                        prod.send(msg, topic);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
//                    if (text.equals(Main.EXIT_MESSAGE)) {
//                        break;
//                    }
                }
            }
        });
        thread.start();
    }

    public static void publishPrice(String message,String targetStock) {
        System.out.println("publishPrice : " + message + " to : " + targetStock);
        queuePricePublisher.add(new Pair<>(message,targetStock));
    }

}
