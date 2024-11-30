package org.example;

import com.solacesystems.jcsmp.JCSMPException;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static String CompanyName;
    public static final String EXIT_MESSAGE = "End of System";
    public static int mode; // 0 for auto 1 for manual

    public static void main(String[] args) throws JCSMPException {
        AskBidPublisher askBidPublisher = new AskBidPublisher();
        ManualUserInterfaceThread manualUserInterfaceThread = new ManualUserInterfaceThread(askBidPublisher);
        manualUserInterfaceThread.start();
    }
}