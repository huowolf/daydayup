package com.huowolf.responsibility;

public class Main {

    public static void main(String[] args) {
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();

        p1.setSuccessor(p2);

        String result = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result);
    }
}
