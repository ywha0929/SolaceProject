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
    private JCSMPSession session;
    private final BlockingDeque<String> queuePricePublisher = new LinkedBlockingDeque<>();
    private Thread thread;

    public PricePublisher(JCSMPSession session) throws JCSMPException {
        this.session = session;
        this.session.connect();
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {

                    String text = null;
                    try {
                        text = queuePricePublisher.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (text.equals(Main.EXIT_MESSAGE)) {
                        break;
                    }
                    System.out.println("running");
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

                    msg.setText(text);
                    try {
                        prod.send(msg, topic);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

    public void publishPrice(String message) {
        System.out.println("publishPrice");
        queuePricePublisher.add(message);
    }

}
