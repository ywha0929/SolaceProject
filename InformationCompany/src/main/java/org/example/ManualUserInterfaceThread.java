package org.example;

import java.util.Scanner;

public class ManualUserInterfaceThread extends Thread {
    public ManualUserInterfaceThread() {

    }

    @Override
    public void run() {
        super.run();
        Scanner sc = new Scanner(System.in);
        Main.companyName   = sc.nextLine();

        while(true) {
            try {
                System.out.println("Choose Stock (Samsung or SK or LG)");
                String stock = sc.nextLine();
                System.out.println("Chosen stock : " + stock);
                PriceViewer priceViewer = new PriceViewer(stock);
                priceViewer.inputThread.join();

            } catch(RuntimeException e) {
                sc.nextLine();
                continue;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
