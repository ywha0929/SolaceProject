package org.example;

import com.google.gson.Gson;

public class CurrentPricing {
    String askbid = "";
    Integer price;
    Integer amount;
    public CurrentPricing (String askbid, Integer price, Integer amount) {
        this.askbid = askbid;
        this.price = price;
        this.amount = amount;
    }
    public String convertToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static CurrentPricing createFromJson(String Json) {
        Gson gson = new Gson();
        return gson.fromJson(Json,CurrentPricing.class);
    }
}
