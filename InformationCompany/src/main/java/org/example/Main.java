package org.example;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main  {
    public static final String EXIT_MESSAGE = "End of System";
    public static String companyName = "";
    public Main() {

    }


    public static void main(String[] args) throws JCSMPException {
        ManualUserInterfaceThread manualUserInterfaceThread = new ManualUserInterfaceThread();
        manualUserInterfaceThread.start();

    }
}