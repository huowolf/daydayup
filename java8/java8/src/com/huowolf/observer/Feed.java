package com.huowolf.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、注册观察者
 * 2、通知观察者
 */
public class Feed implements Subject{
    private final List<Observer> observers = new ArrayList<>();
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }
    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}
