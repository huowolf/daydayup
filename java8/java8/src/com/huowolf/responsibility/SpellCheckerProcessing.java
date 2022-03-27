package com.huowolf.responsibility;

public class SpellCheckerProcessing extends ProcessingObject<String>{
    @Override
    protected String handleWork(String input) {
        return input.replaceAll("labda", "lambda");
    }
}
