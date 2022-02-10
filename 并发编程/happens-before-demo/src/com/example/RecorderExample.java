package com.example;

public class RecorderExample {
    private int x = 0;
    private int y = 1;
    private volatile boolean flag = false;

    public void writer(){
        x = 42;
        y = 50;
        flag = true;
    }

    public void reader(){
        if (flag) {
            System.out.println("x:" + x);
            System.out.println("y:" + y);
        }
    }
}
