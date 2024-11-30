package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import java.util.concurrent.BlockingDeque;

public class AskBidRequestSubscriber extends Thread {
    JCSMPSession session = null;
    BlockingDeque handleRequestThreadQ;
    public AskBidRequestSubscriber(BlockingDeque handleRequestThreadQ) {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
        properties.setProperty(JCSMPProperties.USERNAME, "6G_YEONGWOOHA");
        properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");
        properties.setProperty(JCSMPProperties.PASSWORD, "dkakwek0929!");

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
        this.session = session;
        this.handleRequestThreadQ = handleRequestThreadQ;
    }

    @Override
    public void run() {
        super.run();
        XMLMessageProducer producer;
        try {
            producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                @Override
                public void responseReceived(String messageID) {
                    System.out.println("Producer received response for msg: " + messageID);
                }

                @Override
                public void handleError(String messageID, JCSMPException e, long timestamp) {
                    System.out.printf("Producer received error for msg: %s@%s - %s%n", messageID, timestamp, e);
                }
            });
        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }
        final XMLMessageConsumer cons;
        try {
            cons = session.getMessageConsumer(new XMLMessageListener() {

                @Override
                public void onReceive(BytesXMLMessage msg) {
                    if (msg instanceof TextMessage) {
                        String message = ((TextMessage) msg).getText();

                        String replyString = "OK";
                        TextMessage reply = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                        reply.setText(replyString);
                        try {
                            System.out.println("sending reply");
                            producer.sendReply(msg,reply);
                        } catch (JCSMPException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.printf("TextMessage received: '%s'%n", message);
                    } else {
                        System.out.println("Message received.");
                    }
//                    System.out.printf("Message Dump:%n%s%n", msg.dump());

                }

                @Override
                public void onException(JCSMPException e) {
                    System.out.printf("Consumer received exception: %s%n", e);

                }
            });

            final com.solace.messaging.resources.Topic topic = Topic.of("SK/hi");
            try {
                session.addSubscription((Subscription) topic);
            } catch (JCSMPException e) {
                throw new RuntimeException(e);
            }
            try {
                cons.start();
            } catch (JCSMPException e) {
                throw new RuntimeException(e);
            }

        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }


    }
}
