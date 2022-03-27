package com.huowolf.observer;

public class Main {
    public static void main(String[] args) {
        Feed f = new Feed();
//        f.registerObserver(new NYTimes());
//        f.registerObserver(new Guardian());
//        f.registerObserver(new LeMonde());

        f.registerObserver((String tweet) -> {
            if(tweet != null && tweet.contains("money")){
                System.out.println("Breaking news in NY! " + tweet);
            }
        });
        f.registerObserver((String tweet) -> {
            if(tweet != null && tweet.contains("queen")){
                System.out.println("Yet another news in London... " + tweet);
            }
        });
        f.notifyObservers("The queen said her favourite book is Java 8 in Action!");
    }
}
