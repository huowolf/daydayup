package com.example;

public class SynchronizedExample {
    private int x = 0;

    public void synBlock(){
        // 1.加锁
        synchronized (SynchronizedExample.class){
            x = 1; // 2.对x赋值
        }
        // 3. 解锁
    }
}
