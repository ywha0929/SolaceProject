package org.example;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class HandleRequestThread extends Thread {
    final BlockingDeque<String> queueRequests = new LinkedBlockingDeque<>();
    final Map<String,Map<Integer , List<Integer>>> bidTable = new HashMap<>();
    final Map<String,Map<Integer , List<Integer>>> askTable = new HashMap<>();
    final Map<Integer,String> idToCompany = new HashMap<>();
    Integer id = 0;


    public HandleRequestThread(String[] Stocks) {
        for(String stock : Stocks) {
            Map<Integer, List<Integer>> priceToID = new HashMap<>();
            priceToID.put(0,new ArrayList<Integer>());
            bidTable.put(stock,priceToID);
            Map<Integer,List<Integer>> priceToID2 = new HashMap<>();
            priceToID2.put(0,new ArrayList<Integer>());
            askTable.put(stock,priceToID2);
        }
    }
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

            AskBidRequest newRequest = AskBidRequest.createFromJson(input);
            if(newRequest.askbid.equals("ask")) { // sell
                Map thisBidTable = bidTable.get(newRequest.stock);
                if(newRequest.price == 0) { // match most expensive first
                    Integer[] prices = (Integer[]) thisBidTable.keySet().toArray(new Integer[thisBidTable.size()]);
                    Arrays.sort(prices,Collections.reverseOrder());
                    int priceIndex = 0;

                    if (prices.length == 0) { //시장가인데 sell이 비어있을떄
                        while (newRequest.amount != 0) {
                            Map thisAskTable = askTable.get(newRequest.stock);
                            if (thisAskTable.containsKey(0)) {
                                ((List)thisAskTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            else {
                                List<Integer> newList = new ArrayList<>();
                                thisAskTable.put(0,newList);
                                ((List)thisAskTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            newRequest.amount-=1;
                        }
                    }
                    else if (prices.length == 1 && prices[0] == 0) {
                        Map thisAskTable = askTable.get(newRequest.stock);
                        if (thisAskTable.containsKey(0)) {
                            ((List)thisAskTable.get(0)).add(id);
                            idToCompany.put(id, newRequest.securityCompany);
                            id+=1;
                        }
                        else {
                            List<Integer> newList = new ArrayList<>();
                            thisAskTable.put(0,newList);
                            ((List)thisAskTable.get(0)).add(id);
                            idToCompany.put(id, newRequest.securityCompany);
                            id+=1;
                        }
                        newRequest.amount-=1;
                    }
                    else {
                        if(prices[priceIndex] == 0) { // if cheepest is 0 pass;
                            priceIndex += 1;
                        }
                        while(newRequest.amount != 0) {
                            if(priceIndex >= prices.length) { //add to Bid List
                                Map thisAskTable = askTable.get(newRequest.stock);
                                if (thisAskTable.containsKey(0)) {
                                    ((List)thisAskTable.get(0)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                }
                                else {
                                    List<Integer> newList = new ArrayList<>();
                                    thisAskTable.put(0,newList);
                                    ((List)thisAskTable.get(0)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                }
                                newRequest.amount-=1;
                            }
                            else if( ((List)thisBidTable.get( prices[priceIndex] )).size() > 0 ) {
                                int targetId = (int) ((List)thisBidTable.get( prices[priceIndex] )).get(0);
                                ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, prices[priceIndex], idToCompany.get(targetId));
                                ExchangeConcludePublisher.publishExchangeConclusion(conclusion);
                                newRequest.amount-=1;
                                ((List)thisBidTable.get( prices[priceIndex] )).remove(0);


                                CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, prices[priceIndex]);
//                                System.out.println(String.format(" 1what: %d",prices[priceIndex]));
                                createCurrentPricing.start();





                                if ( ((List)thisBidTable.get( prices[priceIndex] )).size() == 0 ){
                                    priceIndex +=1;
                                }
                            }
                            else {
                                priceIndex +=1;
                            }

                        }
                    }



                }
                else { //find matching price and conclude if no add to ask table
                    if(thisBidTable.containsKey(0) && ((List<Integer>) thisBidTable.get(0)).size() != 0) {
                        int min = Math.min(((List<Integer>) thisBidTable.get(0)).size(), newRequest.amount);
                        List<Integer> idList = (List<Integer>) thisBidTable.get(0);

                        while (min != 0) {
                            int targetId = idList.get(0);
                            ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, newRequest.price, idToCompany.get(targetId));
                            idList.remove(0);
                            newRequest.amount-=1;
                            ExchangeConcludePublisher.publishExchangeConclusion(conclusion);

                            CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, newRequest.price);
//                            System.out.println(String.format(" 2what: %d",newRequest.price));
                            createCurrentPricing.start();
                            min-=1;
                        }
                    }



                    if(thisBidTable.containsKey(newRequest.price)){ //found matching price;
                        while(newRequest.amount != 0) {
                            List<Integer> idList = (List<Integer>) thisBidTable.get(newRequest.price);
                            if(idList.size() == 0) {//empty add to askList;
                                Map thisAskTable = askTable.get(newRequest.stock);
                                if(thisAskTable.containsKey(newRequest.price)) {
                                    ((List)thisAskTable.get(newRequest.price)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                    newRequest.amount-=1;
                                }
                                else {
                                    List<Integer> newList = new ArrayList<>();
                                    thisAskTable.put(newRequest.price,newList);
                                    ((List)thisAskTable.get(newRequest.price)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                    newRequest.amount-=1;
                                }
                            }
                            else { //conclude found amount
                                int targetId = idList.get(0);


                                ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, newRequest.price, idToCompany.get(targetId));
                                ExchangeConcludePublisher.publishExchangeConclusion(conclusion);
//                                ExchangeConcludePublisher.publishExchangeConclusion(String.format("%d : %s",targetId,idToCompany.get(targetId)));
                                idList.remove(0);
                                newRequest.amount-=1;

                                CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, newRequest.price);
//                                System.out.println(String.format(" 3what: %d",newRequest.price));
                                createCurrentPricing.start();

                            }
                        }
                    }
                    else { //no matching price in bidList
                        while(newRequest.amount != 0) {
                            Map thisAskTable = askTable.get(newRequest.stock);
                            if(!thisAskTable.containsKey(newRequest.price)) {
                                List<Integer> newList = new ArrayList<>();
                                thisAskTable.put(newRequest.price,newList);
                            }
                            ((List)thisAskTable.get(newRequest.price)).add(id);
                            idToCompany.put(id, newRequest.securityCompany);
                            id+=1;
                            newRequest.amount-=1;
                        }
                    }
                }
            }
            else { // bid buy
                Map thisAskTable = askTable.get(newRequest.stock);
                if(newRequest.price == 0) { // match most expensive first
                    Integer[] prices = (Integer[]) thisAskTable.keySet().toArray(new Integer[thisAskTable.size()]);
                    Arrays.sort(prices);
                    int priceIndex = 0;
                    if (prices.length == 0) { //시장가인데 sell이 비어있을떄
                        while (newRequest.amount != 0) {
                            Map thisBidTable = bidTable.get(newRequest.stock);
                            if (thisBidTable.containsKey(0)) {
                                ((List)thisBidTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            else {
                                List<Integer> newList = new ArrayList<>();
                                thisBidTable.put(0,newList);
                                ((List)thisBidTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            newRequest.amount-=1;
                        }
                    }
                    else if (prices.length == 1 && prices[0] == 0) {
                        while (newRequest.amount != 0) {
                            Map thisBidTable = bidTable.get(newRequest.stock);
                            if (thisBidTable.containsKey(0)) {
                                ((List)thisBidTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            else {
                                List<Integer> newList = new ArrayList<>();
                                thisBidTable.put(0,newList);
                                ((List)thisBidTable.get(0)).add(id);
                                idToCompany.put(id, newRequest.securityCompany);
                                id+=1;
                            }
                            newRequest.amount-=1;
                        }
                    }
                    else {
                        if(prices[priceIndex] == 0) { // if cheepest is 0 pass;
                            priceIndex += 1;
                        }
                        while(newRequest.amount != 0) {
                            if(priceIndex >= prices.length) { //add to Bid List
                                Map thisBidTable = bidTable.get(newRequest.stock);
                                if (thisBidTable.containsKey(0)) {
                                    ((List)thisBidTable.get(0)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                }
                                else {
                                    List<Integer> newList = new ArrayList<>();
                                    thisBidTable.put(0,newList);
                                    ((List)thisBidTable.get(0)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                }
                                newRequest.amount-=1;
                            }
                            else if( ((List)thisAskTable.get( prices[priceIndex] )).size() > 0 ) {
                                int targetId = (int) ((List)thisAskTable.get( prices[priceIndex] )).get(0);

                                ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, prices[priceIndex], idToCompany.get(targetId));
                                ExchangeConcludePublisher.publishExchangeConclusion(conclusion);

//                            ExchangeConcludePublisher.publishExchangeConclusion(String.format("%d : %s",targetId,idToCompany.get(id)));
                                newRequest.amount-=1;
                                ((List)thisAskTable.get( prices[priceIndex] )).remove(0);

                                CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, prices[priceIndex]);
//                                System.out.println(String.format(" 4what: %d",prices[priceIndex]));
                                createCurrentPricing.start();


                                if ( ((List)thisAskTable.get( prices[priceIndex] )).size() == 0 ){
                                    priceIndex +=1;
                                }
                            }
                            else {
                                priceIndex +=1;
                            }

                        }
                    }

                }
                else { //find matching price and conclude if no add to ask table
                    if(thisAskTable.containsKey(0) && ((List<Integer>) thisAskTable.get(0)).size() != 0) {
                        int min = Math.min(((List<Integer>) thisAskTable.get(0)).size(), newRequest.amount);
                        List<Integer> idList = (List<Integer>) thisAskTable.get(0);

                        while (min != 0) {
                            int targetId = idList.get(0);
                            ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, newRequest.price, idToCompany.get(targetId));
                            idList.remove(0);
                            newRequest.amount-=1;

                            CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, newRequest.price);
//                            System.out.println(String.format(" 5what: %d",newRequest.price));
                            createCurrentPricing.start();

                            ExchangeConcludePublisher.publishExchangeConclusion(conclusion);
                            min -= 1;
                        }
                    }

                    if(thisAskTable.containsKey(newRequest.price)){ //found matching price;
                        while(newRequest.amount != 0) {
                            List<Integer> idList = (List<Integer>) thisAskTable.get(newRequest.price);
                            if(idList.size() == 0) {//empty add to askList;
                                Map thisBidTable = bidTable.get(newRequest.stock);
                                if(thisBidTable.containsKey(newRequest.price)) {
                                    ((List)thisBidTable.get(newRequest.price)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                    newRequest.amount-=1;
                                }
                                else {
                                    List<Integer> newList = new ArrayList<>();
                                    thisBidTable.put(newRequest.price,newList);
                                    ((List)thisBidTable.get(newRequest.price)).add(id);
                                    idToCompany.put(id, newRequest.securityCompany);
                                    id+=1;
                                    newRequest.amount-=1;
                                }
                            }
                            else { //conclude found amount
                                int targetId = idList.get(0);

                                ExchangeConclusionMessage conclusion = new ExchangeConclusionMessage(newRequest.stock, newRequest.price, idToCompany.get(targetId));
                                ExchangeConcludePublisher.publishExchangeConclusion(conclusion);

//                                ExchangeConcludePublisher.publishExchangeConclusion(String.format("%d : %s",targetId,idToCompany.get(targetId)));
                                idList.remove(0);
                                newRequest.amount-=1;

                                CreateCurrentPricing createCurrentPricing = new CreateCurrentPricing(this, newRequest.stock, newRequest.price);
//                                System.out.println(String.format(" 6what: %d",newRequest.price));
                                createCurrentPricing.start();
                            }
                        }
                    }
                    else { //no matching price in bidList
                        while(newRequest.amount != 0) {
                            Map thisBidTable = bidTable.get(newRequest.stock);
                            if(!thisBidTable.containsKey(newRequest.price)) {
                                List<Integer> newList = new ArrayList<>();
                                thisBidTable.put(newRequest.price,newList);
                            }
                            ((List)thisBidTable.get(newRequest.price)).add(id);
                            idToCompany.put(id, newRequest.securityCompany);
                            id+=1;
                            newRequest.amount-=1;
                        }
                    }
                }

            }

        }
    }
}

class CreateCurrentPricing extends Thread{

    HandleRequestThread caller;
    String targetStock;
    Integer latestPrice;
    public CreateCurrentPricing(HandleRequestThread caller,String targetStock,Integer latestPrice) {
        this.caller = caller;
        this.targetStock = targetStock;
        this.latestPrice = latestPrice;
    }

    @Override
    public void run() {
        super.run();
//        System.out.println(String.format(" what: %d",latestPrice));
        Integer[] bidPrices = caller.bidTable.get(targetStock).keySet().toArray(new Integer[caller.bidTable.get(targetStock).size()]);
        Integer[] askPrices = caller.askTable.get(targetStock).keySet().toArray(new Integer[caller.askTable.get(targetStock).size()]);
        Arrays.sort(askPrices,Collections.reverseOrder());
        Arrays.sort(bidPrices,Collections.reverseOrder());
        CurrentPricingMessage message = new CurrentPricingMessage(latestPrice);
        for(Integer ask : askPrices) {
            CurrentPricing currentPricing = new CurrentPricing("ask",ask, caller.askTable.get(targetStock).get(ask) == null ? 0 : caller.askTable.get(targetStock).get(ask).size());
            message.addToList(currentPricing);
        }
        for(Integer bid : bidPrices) {
            CurrentPricing currentPricing = new CurrentPricing("bid",bid, caller.bidTable.get(targetStock).get(bid) == null ? 0 : caller.bidTable.get(targetStock).get(bid).size());
            message.addToList(currentPricing);
        }

        PricePublisher.publishPrice(message.convertToJson(),targetStock);

    }
}

