package org.example;

import com.solace.messaging.resources.Topic;
import com.solacesystems.jcsmp.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ConclusionSubscriberWithNotification extends JFrame {
    public ConclusionSubscriberWithNotification() {
        JPanel middlePanel = new JPanel ();
        middlePanel.setBorder ( new TitledBorder( new EtchedBorder(), "Notification Area" ) );

        // create the middle panel components

        JTextArea display = new JTextArea ( 16, 58 );
        display.setEditable ( false ); // set textArea non-editable
        JScrollPane scroll = new JScrollPane ( display );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        //Add Textarea in to middle panel
        middlePanel.add ( scroll );

        // My code
        JFrame frame = new JFrame ();
        frame.add ( middlePanel );
        frame.pack ();
        frame.setLocationRelativeTo ( null );
        frame.setVisible ( true );

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final JCSMPProperties properties = new JCSMPProperties();
                properties.setProperty(JCSMPProperties.HOST,"tcp://mr-connection-vht20gwjoky.messaging.solace.cloud:55555");
                properties.setProperty(JCSMPProperties.USERNAME, "SecurityCompany"+Main.CompanyName);
                properties.setProperty(JCSMPProperties.VPN_NAME,  "ai6g");
                properties.setProperty(JCSMPProperties.PASSWORD, "SecurityCompany"+Main.CompanyName);

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

                final ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();

                Queue queue = JCSMPFactory.onlyInstance().createQueue(String.format("Q_SecurityCompany%s",Main.CompanyName));
                flow_prop.setEndpoint(queue);
                flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);
                EndpointProperties endpoint_props = new EndpointProperties();
                endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
                final FlowReceiver cons;
                try {
                    cons = session.createFlow(new XMLMessageListener() {
                        int linenumber = 1;
                        @Override
                        public void onReceive(BytesXMLMessage msg) {
                            if (msg instanceof TextMessage) {
                                ExchangeConclusionMessage message = ExchangeConclusionMessage.createFromJson(((TextMessage) msg).getText());
                                display.append(String.format("%d. Exchange Concluded : \n    Stock : %s\n    price : %d\n",linenumber, message.stock,message.price));
                                linenumber+=1;
//                                System.out.printf("TextMessage received: '%s'%n", ((TextMessage) msg).getText());
                            } else {
//                                System.out.println("Message received.");
                            }
//                            System.out.printf("Message Dump:%n%s%n", msg.dump());
                            // When the ack mode is set to SUPPORTED_MESSAGE_ACK_CLIENT,
                            // guaranteed delivery messages are acknowledged after
                            // processing
                            msg.ackMessage();

                        }

                        @Override
                        public void onException(JCSMPException e) {
                            System.out.printf("Consumer received exception: %s%n", e);
                        }
                    }, flow_prop, endpoint_props);
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                try {
                    cons.start();
                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }


            }
        });
        thread.start();
    }
}
