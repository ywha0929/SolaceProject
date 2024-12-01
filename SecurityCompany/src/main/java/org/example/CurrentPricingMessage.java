package org.example;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CurrentPricingMessage {
    List<CurrentPricing> list;
    Integer latestPrice = 0;
    public CurrentPricingMessage(Integer latestPrice) {
        list = new ArrayList<>();
        latestPrice = latestPrice;
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

    public String[] toStringList() {
        List<String> lines = new ArrayList<>();
        for(CurrentPricing l : list) {
            if(l.askbid.equals("ask")) {
                lines.add(l.toString());
            }
        }
        while(lines.size() != 13) {
            lines.add(0,"\n");
        }
        lines.add(String.format("%18s : %2d","Conclusion Price\n",latestPrice));
        for(CurrentPricing l : list) {
            if(l.askbid.equals("bid")) {
                lines.add(l.toString());
            }
        }
        while(lines.size() !=27) {
            lines.add(lines.size()-1,"\n");
        }
        return (String[]) lines.toArray();

    }

}
