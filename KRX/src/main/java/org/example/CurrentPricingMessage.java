package org.example;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CurrentPricingMessage {
    List<CurrentPricing> list;
    Integer latestPrice = 0;
    public CurrentPricingMessage(Integer latestPrice) {
        list = new ArrayList<>();
        this.latestPrice = latestPrice;
    }
    public void addToList(CurrentPricing pricing) {
        list.add(pricing);
    }
    public String convertToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static CurrentPricingMessage createFromJson(String Json) {
        Gson gson = new Gson();
        return gson.fromJson(Json,CurrentPricingMessage.class);
    }
}
