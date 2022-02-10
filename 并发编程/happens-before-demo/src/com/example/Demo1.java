package com.example;

public class Demo1 {


    public static void main(String[] args) throws InterruptedException {
        RecorderExample recorderExample = new RecorderExample();
        Thread t1 = new Thread(()->{
            recorderExample.writer();
        });
        Thread t2 = new Thread(()->{
            recorderExample.reader();
        });

        t2.start();
        t1.start();

        t1.join();
        t2.join();
    }
}
