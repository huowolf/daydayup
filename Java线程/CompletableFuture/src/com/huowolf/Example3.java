package com.huowolf;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 可以一个任务执行完就执行下一个阶段的任务，而不是等所有任务执行完才执行下一个阶段的任务
 */
public class Example3 {

    public static void main(String[] args) throws InterruptedException {
        IntStream.range(0, 10).boxed()
                .forEach(i -> CompletableFuture.supplyAsync(Example3::get)
                        .thenAccept(Example3::display)
                        .whenComplete((v,t) -> System.out.println(i +" Done!")));

        Thread.currentThread().join();
    }


    private static int get(){
        int value = ThreadLocalRandom.current().nextInt(20);
        try {
            System.out.println(Thread.currentThread().getName() + " get will be sleep "+ value);
            TimeUnit.SECONDS.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " get execute done "+ value);
        return value;
    }

    private static void display(int data){
        int value = ThreadLocalRandom.current().nextInt(20);
        try {
            System.out.println(Thread.currentThread().getName() + " display will be sleep "+ value);
            TimeUnit.SECONDS.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " display execute done "+ data);
    }
}
