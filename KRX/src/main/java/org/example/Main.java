package org.example;

import com.solacesystems.jcsmp.JCSMPException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String EXIT_MESSAGE = "End of System";
    public static void main(String[] args) throws JCSMPException {

//        PricePublisher pricePublisher = new PricePublisher();
        String[] stockSet1 = {"Samsung","LG"};
        HandleRequestThread handleRequestThreadSamsungLG = new HandleRequestThread(stockSet1);
        AskBidRequestSubscriber askBidRequestSubscriberSamsungLG = new AskBidRequestSubscriber(handleRequestThreadSamsungLG.queueRequests,stockSet1);
        handleRequestThreadSamsungLG.start();
        askBidRequestSubscriberSamsungLG.start();

        String[] stockSet2 = {"SK"};
        HandleRequestThread handleRequestThreadSK = new HandleRequestThread(stockSet2);
        AskBidRequestSubscriber askBidRequestSubscriberSK = new AskBidRequestSubscriber(handleRequestThreadSK.queueRequests,stockSet2);
        handleRequestThreadSK.start();
        askBidRequestSubscriberSK.start();

        ExchangeConcludePublisher exchangeConcludePublisher = new ExchangeConcludePublisher();
        PricePublisher pricePublisher = new PricePublisher();

//        //for testing
//        ConsoleThread consoleThread = new ConsoleThread(pricePublisher);
//        consoleThread.start();
    }
}

