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

        ConclusionSubscriberWithNotification conclusionSubscriberWithNotification = new ConclusionSubscriberWithNotification();
        while(true) {
            try {
                System.out.println("Choose Stock (Samsung or SK or LG)");
                String stock = sc.next();
                System.out.println("Choose to watch current price, or ask/bid (0 for watch, 1 for ask/bid)");
                int mode = sc.nextInt();
                sc.nextLine();
                if (mode == 0) {
                    PriceViewer priceViewer = new PriceViewer(stock);
                    priceViewer.inputThread.join();
                } else {
                    System.out.println("Choose ask(sell)/bid(buy)");
                    String askbid = sc.next();
                    System.out.println("Choose price (0 for preferred price)");
                    int price = sc.nextInt();
                    System.out.println("Choose amount");
                    int amount = sc.nextInt();
                    AskBidRequest request = new AskBidRequest(stock, askbid, price, amount);
                    askBidPublisher.publishPrice(request);

                }
            } catch(RuntimeException e) {
                sc.nextLine();
                continue;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
