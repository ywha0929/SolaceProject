package org.example;

import com.solacesystems.jcsmp.JCSMPException;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class PriceViewer extends JFrame {
    private final BlockingDeque<String> queuePriceReceived = new LinkedBlockingDeque<>();
    private JTextField outputField;
    public PriceViewer() {
        setTitle("Information Company");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300); // 프레임 크기 설정
        Container contentPane = getContentPane(); //프레임에서 컨텐트팬 받아오기
        contentPane.setLayout(null);
        JTextField textField2 = new JTextField();
        textField2.setBounds (50,20,400,200);
        textField2.setEnabled(false);
        contentPane.add(textField2);
        outputField = textField2;

        setVisible(true);

        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);

                while(true) {

                    System.out.println("Choose Stock (Samsung, SK)");
                    String stock = sc.next();
                    PriceSubscriber priceSubscriber;;
                    try {
                        priceSubscriber = new PriceSubscriber(outputField,stock);
                        priceSubscriber.start();

                    } catch (JCSMPException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("enter c for exit : ");
                    while(!sc.next().equals("c")){

                    }
                    try {
                        PriceSubscriber.sleep(10L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    priceSubscriber.interrupt();
                }
            }
        });
        inputThread.start();

    }

//    @Override
//    public void run() {
//        super.run();
//        while (true) {
//            String text = null;
//            try{
//                text = queuePriceReceived.take();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            if (text.equals(Main.EXIT_MESSAGE)) {
//                break;
//            }
//            System.out.println(String.format("Current Price :\n%s",text));
//
//        }
//    }
    public void putPriceViewerQueue(String message) {
        this.queuePriceReceived.add(message);
    }
}
