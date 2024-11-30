package org.example;

public class AskBidRequest {
    String stock;
    int askbid; // 0 for ask(sell) 1 for bid (buy)
    int price;
    public AskBidRequest(String stock, int askbid, int price) {
        this.stock = stock;
        this.askbid = askbid;
        this.price = price;
    }
}
