package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PriceSubscriber extends Thread {
    JCSMPSession session;
    ArrayList<JTextField> outputField;
    //    Queue priceViewerQ;
    private final String TOPIC = "/*";
    String stock;

    public PriceSubscriber(ArrayList<JTextField> outputField, String stock) throws JCSMPException {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
        properties.setProperty(JCSMPProperties.USERNAME, "InformationCompany"+Main.companyName);
        properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");
        properties.setProperty(JCSMPProperties.PASSWORD, "InformationCompany"+Main.companyName);

        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);


        this.session = session;
        this.session.connect();
        this.outputField = outputField;
        this.stock = stock;
    }

    @Override
    public void run() {
        super.run();


        final XMLMessageConsumer cons;
        try {
            cons = session.getMessageConsumer(new XMLMessageListener() {

                @Override
                public void onReceive(BytesXMLMessage msg) {
                    if (msg instanceof TextMessage) {
                        String message = ((TextMessage) msg).getText();
                        String[] print = CurrentPricingMessage.createFromJson(message).toStringList();
                        for (int i = 0; i< 27; i++) {
                            outputField.get(i).setText(print[i]);
                        }

                        if (message.equals(Main.EXIT_MESSAGE))
                            System.out.println("EndofSystem");
//                        System.out.printf("TextMessage received: '%s'%n", message);
                    } else {
//                        System.out.println("Message received.");
                    }
//                    System.out.printf("Message Dump:%n%s%n", msg.dump());

                }

                @Override
                public void onException(JCSMPException e) {
                    System.out.printf("Consumer received exception: %s%n", e);

                }
            });
        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }
        final Topic topic = Topic.of(String.format("currentpricing/%s",stock));
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

    }
}


