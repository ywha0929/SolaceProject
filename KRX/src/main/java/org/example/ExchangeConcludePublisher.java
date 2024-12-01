package org.example;

import com.solacesystems.jcsmp.*;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ExchangeConcludePublisher {
    private final static BlockingDeque<ExchangeConclusionMessage> queueExhangeConcludeQ = new LinkedBlockingDeque<>();

    public ExchangeConcludePublisher() throws InvalidPropertiesException {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
        properties.setProperty(JCSMPProperties.USERNAME, "YWHA_KRX");
        properties.setProperty(JCSMPProperties.PASSWORD, "KRX");
        properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");


        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);






        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XMLMessageProducer prod;
                try {
                    prod= session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {

                        @Override
                        public void responseReceived(String messageID) {
                            System.out.println("Producer received response for msg: " + messageID);
                        }

                        @Override
                        public void handleError(String messageID, JCSMPException e, long timestamp) {
                            System.out.printf("Producer received error for msg: %s@%s - %s%n",messageID,timestamp,e);
                        }
                    });
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }


                while(true) {
                    ExchangeConclusionMessage Conclusion=null;

                    try{
                        Conclusion = queueExhangeConcludeQ.take();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Queue queue = JCSMPFactory.onlyInstance().createQueue(String.format("Q_SecurityCompany%s",Conclusion.securityCompany));
                    TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
                    msg.setDeliveryMode(DeliveryMode.PERSISTENT);
                    String payload = Conclusion.convertToJson();
                    msg.setText(payload);
                    try {
                        prod.send(msg,queue);
                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }

//                    System.out.println(Conclusion);
                }
            }
        });
        thread.start();
    }



    public static void publishExchangeConclusion(ExchangeConclusionMessage message) {
        System.out.println("publishExchangeConclusion");
        queueExhangeConcludeQ.add(message);
    }
}
