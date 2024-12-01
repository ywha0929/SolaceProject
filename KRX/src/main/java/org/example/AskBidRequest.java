package org.example;

import com.google.gson.Gson;

public class AskBidRequest {
    String securityCompany;
    String stock;
    String askbid; // 0 for ask(sell) 1 for bid (buy)
    int price;
    int amount;


    public static AskBidRequest createFromJson(String Json) {
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
