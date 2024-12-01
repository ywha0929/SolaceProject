package org.example;

import java.util.Scanner;

import static java.lang.System.exit;

public class ConsoleThread extends Thread {
    Scanner sc = new Scanner(System.in);
    PricePublisher pricePublisher;
    public ConsoleThread(PricePublisher pricePublisher) {
        this.pricePublisher = pricePublisher;
    }
    @Override
    public void run() {
        super.run();
        while(true) {
            Scanner sc = new Scanner(System.in);
            String input = sc.next();
            if(input.equals("exit")) {
//                pricePublisher.publishPrice("End of System");
                exit(0);
            }
            else {
                System.out.println("publishing : "+input);
//                pricePublisher.publishPrice(input);
            }
        }
    }
}
