package com.huowolf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Example1 {

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).whenComplete((v,t) -> System.out.println("Done!"));
        System.out.println("========I am not blocked=============");

        Thread.currentThread().join();
    }
}
