package org.example;

import com.google.gson.Gson;

public class AskBidRequest {
    String securityCompany;
    String stock;
    String askbid; // 0 for ask(sell) 1 for bid (buy)
    int price;
    int amount;
    public AskBidRequest(String stock, String askbid, int price, int amount) {
        this.securityCompany = Main.CompanyName;
        this.stock = stock;
        this.askbid = askbid;
        this.price = price;
        this.amount = amount;
    }

    public AskBidRequest createFromJson(String Json) {
        Gson gson = new Gson();
        AskBidRequest askBidRequest = gson.fromJson(Json, AskBidRequest.class);
        return askBidRequest;
    }
    public String convertToJson() {
        Gson gson = new Gson();
        String result = gson.toJson(this);

        return result;
    }
}
