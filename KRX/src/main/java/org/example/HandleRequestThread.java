package org.example;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class HandleRequestThread extends Thread {
    final BlockingDeque<String> queueRequests = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        super.run();
        while(true) {
            String input = null;
            try{
                input = queueRequests.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(String.format("Handling input : %s", input));
        }
    }
}
