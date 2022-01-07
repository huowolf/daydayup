package com.huowolf.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {

    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<String>(new MyCallable());
        new Thread(futureTask).start();
        if (!futureTask.isDone()) {
            System.out.println("task has not finished.");
        }
        try {
            System.out.println("task return:" + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
