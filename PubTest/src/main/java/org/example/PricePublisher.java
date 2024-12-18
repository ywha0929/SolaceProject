package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PricePublisher {
    private JCSMPSession session;
    private Queue queuePricePublisher = new LinkedBlockingQueue<String>();

    public PricePublisher(JCSMPSession session) throws JCSMPException {
        this.session = session;
        this.session.connect();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    while (queuePricePublisher.isEmpty()) ;
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
                    final Topic topic = Topic.of("data/test");
                    TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                    final String text = "Hello world!";
                    msg.setText(text);
                    try {
                        prod.send(msg, topic);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void publishPrice(String message) {
        queuePricePublisher.add(message);
    }

}
