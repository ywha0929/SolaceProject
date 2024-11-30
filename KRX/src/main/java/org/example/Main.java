package org.example;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;

import java.util.Scanner;

import static java.lang.System.exit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String EXIT_MESSAGE = "End of System";
    public static void main(String[] args) throws JCSMPException {

//        PricePublisher pricePublisher = new PricePublisher();
        HandleRequestThread handleRequestThread = new HandleRequestThread();
        AskBidRequestSubscriber askBidRequestSubscriber = new AskBidRequestSubscriber(handleRequestThread.queueRequests);
        handleRequestThread.start();
        askBidRequestSubscriber.start();

//        //for testing
//        ConsoleThread consoleThread = new ConsoleThread(pricePublisher);
//        consoleThread.start();
    }
}

