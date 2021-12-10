package com.demo1;

public class demo1 {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (true) {
                Thread.yield();
            }
        });
        thread.start();
        thread.interrupt();
    }
}
