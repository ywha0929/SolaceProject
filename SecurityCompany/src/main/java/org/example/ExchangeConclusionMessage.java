package org.example;

import com.google.gson.Gson;

public class ExchangeConclusionMessage {
    String stock;
    int price;
    String securityCompany;
    String askbid;
    public ExchangeConclusionMessage(String stock, int price, String securityCompany,String askbid) {
        this.stock = stock;
        this.price = price;
        this.securityCompany = securityCompany;
        this.askbid = askbid;
    }
    public String convertToJson() {
        Gson gson = new Gson();
        String result = gson.toJson(this);
        return result;

    }
    public static ExchangeConclusionMessage createFromJson(String Json) {
        Gson gson = new Gson();

        return gson.fromJson(Json,ExchangeConclusionMessage.class);
    }
}
