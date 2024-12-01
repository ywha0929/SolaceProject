package org.example;

import com.solacesystems.jcsmp.JCSMPException;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class PriceViewer extends JFrame {
    private final BlockingDeque<String> queuePriceReceived = new LinkedBlockingDeque<>();
    private ArrayList<JTextField> outputFields = new ArrayList<>();
    String stock;
    Scanner sc;
    Thread inputThread;
    public PriceViewer(String stock) {
        setTitle("Information Company"+Main.companyName+" : " + stock);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700); // 프레임 크기 설정
        Container contentPane = getContentPane(); //프레임에서 컨텐트팬 받아오기
        contentPane.setLayout(new GridLayout(27,1));

        for(int i = 0; i< 27; i++) {
            outputFields.add(new JTextField());
            if(i >= 0 && i <= 12)
                outputFields.get(i).setForeground(Color.RED);
            else if(i == 13) {
                outputFields.get(i).setForeground(Color.BLACK);
            }
            else
                outputFields.get(i).setForeground(Color.BLUE);

        }
        for(JTextField outputField : outputFields) {
            contentPane.add(outputField);
        }


        setVisible(true);
        this.stock = stock;
        this.sc = new Scanner(System.in);
        inputThread = new Thread(new Runnable() {
            @Override
            public void run() {


                PriceSubscriber priceSubscriber;;
                try {
                    priceSubscriber = new PriceSubscriber(outputFields,stock);
                    priceSubscriber.start();

                } catch (JCSMPException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("enter any for exit : ");
                sc.nextLine();

                try {
                    PriceSubscriber.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                priceSubscriber.interrupt();
                try {
                    priceSubscriber.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
