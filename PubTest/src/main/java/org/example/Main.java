package org.example;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws JCSMPException {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
        properties.setProperty(JCSMPProperties.USERNAME, "6G_YEONGWOOHA");
        properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");
        properties.setProperty(JCSMPProperties.PASSWORD, "dkakwek0929!");

        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
        PricePublisher pricePublisher = new PricePublisher(session);
        session.connect();

        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {

            @Override
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }

            @Override
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",messageID,timestamp,e);
            }
        });
        final Queue queue = JCSMPFactory.onlyInstance().createQueue("Q_YEONGWOOHA");
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
        String text = "Persistent Queue Tutorial! " +
                DateFormat.getDateTimeInstance().format(new Date());
        msg.setText(text);
// Delivery not yet confirmed. See ConfirmedPublish.java
        prod.send(msg, queue);



    }
}


// send direct message
//        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
//
//            @Override
//            public void responseReceived(String messageID) {
//                System.out.println("Producer received response for msg: " + messageID);
//            }
//
//            @Override
//            public void handleError(String messageID, JCSMPException e, long timestamp) {
//                System.out.printf("Producer received error for msg: %s@%s - %s%n",
//                        messageID,timestamp,e);
//            }
//        });
//        final Topic topic = Topic.of("data/test");
//        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
//        final String text = "Hello world!";
//        msg.setText(text);
//        prod.send(msg,topic);