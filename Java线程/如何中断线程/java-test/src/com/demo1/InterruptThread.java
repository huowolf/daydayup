package com.demo1;

public class InterruptThread extends Thread{
    public volatile boolean running = true;  //重点:volatile

    public static void main(String[] args) throws InterruptedException {
        InterruptThread interruptThread = new InterruptThread();
        interruptThread.start();
        Thread.sleep(10);
        interruptThread.running = false;
    }

    public void run(){
        int n = 0;
        while (running) {
            n++;
            System.out.println(n + " hello!");
        }
        System.out.println("end!");
    }
}
