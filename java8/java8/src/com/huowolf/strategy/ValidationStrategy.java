package com.huowolf.strategy;

@FunctionalInterface
public interface ValidationStrategy {
    boolean execute(String s);
}
