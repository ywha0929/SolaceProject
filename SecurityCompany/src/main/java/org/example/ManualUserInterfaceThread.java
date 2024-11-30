package org.example;

import java.util.Scanner;

public class ManualUserInterfaceThread extends Thread {
    AskBidPublisher askBidPublisher;
    public ManualUserInterfaceThread(AskBidPublisher askBidPublisher) {
        this.askBidPublisher = askBidPublisher;
    }

    @Override
    public void run() {
        super.run();
        Scanner sc = new Scanner(System.in);
        Main.CompanyName =sc.next();
        while(true) {

            System.out.println("Choose Stock (Samsung or SK)");
            String stock = sc.next();
            System.out.println("Choose to watch current price, or ask/bid (0 for watch, 1 for ask/bid)");
            int mode = sc.nextInt();
            if(mode == 0) {
                PriceViewer priceViewer = new PriceViewer(stock,sc);
            }
            else {
                System.out.println("Choose ask(sell)/bid(buy) (0 for ask, 1 for bid)");
                int askbid = sc.nextInt();
                System.out.println("Choose price (0 for preferred price");
                int price = sc.nextInt();
                AskBidRequest request = new AskBidRequest(stock,askbid,price);
                askBidPublisher.publishPrice(request);

            }
        }
    }
}
