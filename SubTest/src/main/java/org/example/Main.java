package org.example;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.receiver.DirectMessageReceiver;
import com.solace.messaging.receiver.InboundMessage;
import com.solace.messaging.receiver.MessageReceiver;
import com.solace.messaging.resources.Topic;
import com.solace.messaging.resources.TopicSubscription;
import com.solacesystems.jcsmp.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

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

        session.connect();

//        final ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
//        final CountDownLatch latch = new CountDownLatch(2);
//        Queue queue = JCSMPFactory.onlyInstance().createQueue("Q_YEONGWOOHA");
//        flow_prop.setEndpoint(queue);
//        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);
//        EndpointProperties endpoint_props = new EndpointProperties();
//        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
//        final FlowReceiver cons = session.createFlow(new XMLMessageListener() {
//            @Override
//            public void onReceive(BytesXMLMessage msg) {
//                if (msg instanceof TextMessage) {
//                    System.out.printf("TextMessage received: '%s'%n", ((TextMessage) msg).getText());
//                } else {
//                    System.out.println("Message received.");
//                }
//                System.out.printf("Message Dump:%n%s%n", msg.dump());
//                // When the ack mode is set to SUPPORTED_MESSAGE_ACK_CLIENT,
//                // guaranteed delivery messages are acknowledged after
//                // processing
//                msg.ackMessage();
//                latch.countDown(); // unblock main thread
//            }
//
//            @Override
//            public void onException(JCSMPException e) {
//                System.out.printf("Consumer received exception: %s%n", e);
//                latch.countDown(); // unblock main thread
//            }
//        }, flow_prop, endpoint_props);
//        cons.start();
//        try {
//            latch.await(); // block here until message received, and latch will flip
//        } catch (InterruptedException e) {
//            System.out.println("I was awoken while waiting");
//        }


        final CountDownLatch latch = new CountDownLatch(100);

        final XMLMessageConsumer cons = session.getMessageConsumer(new XMLMessageListener() {

            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
                    System.out.printf("TextMessage received: '%s'%n",
                            ((TextMessage)msg).getText());
                } else {
                    System.out.println("Message received.");
                }
                System.out.printf("Message Dump:%n%s%n",msg.dump());
                latch.countDown();  // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.printf("Consumer received exception: %s%n",e);
                latch.countDown();  // unblock main thread
            }
        });
        final Topic topic =Topic.of("data/test");
        session.addSubscription((Subscription) topic);
        cons.start();
        try {
                latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
        System.out.println("I was awoken while waiting");
        }
    }
}
// direct message
//
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        final XMLMessageConsumer cons = session.getMessageConsumer(new XMLMessageListener() {
//
//            @Override
//            public void onReceive(BytesXMLMessage msg) {
//                if (msg instanceof TextMessage) {
//                    System.out.printf("TextMessage received: '%s'%n",
//                            ((TextMessage)msg).getText());
//                } else {
//                    System.out.println("Message received.");
//                }
//                System.out.printf("Message Dump:%n%s%n",msg.dump());
//                latch.countDown();  // unblock main thread
//            }
//
//            @Override
//            public void onException(JCSMPException e) {
//                System.out.printf("Consumer received exception: %s%n",e);
//                latch.countDown();  // unblock main thread
//            }
//        });
//        final Topic topic =Topic.of("data/test");
//        session.addSubscription((Subscription) topic);
//        cons.start();
//        try {
//                latch.await(); // block here until message received, and latch will flip
//        } catch (InterruptedException e) {
//        System.out.println("I was awoken while waiting");
//        }