package org.example;

import com.solacesystems.jcsmp.*;

public class AutoAskBidThread extends Thread{
    String[] stocks = {"Samsung","LG","SK","SK"};
    String[] askbids = {"ask","bid"};
    Integer[] prices = {0,5,10,15,20,25,30,35,40,45,50,55,60};
    AskBidPublisher askBidPublisher;

    public AutoAskBidThread(AskBidPublisher askBidPublisher) {
        this.askBidPublisher = askBidPublisher;
    }
    @Override
    public void run() {
        super.run();
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
                                System.out.println(String.format("%d. Exchange Concluded : \n    Stock : %s\n    price : %d\n",linenumber, message.stock,message.price));
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



        while(true) {
            int stocksRandom = (int)(Math.random()*4);
            String stock = stocks[stocksRandom];
            String askbid = askbids[(int) (Math.random()*2)];
            Integer price = prices[(int) (Math.random()*13)];
            int amount = ((int) (Math.random()*10))+1;
            System.out.println(String.format("Creating request (%s, %s, price : %d, amount :  %d)",stock,askbid,price,amount));
            askBidPublisher.publishPrice(new AskBidRequest(stock,askbid,price,amount));
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
