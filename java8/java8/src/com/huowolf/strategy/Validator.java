package com.huowolf.strategy;

public class Validator {

    ValidationStrategy validationStrategy;

    public Validator(ValidationStrategy validationStrategy){
        this.validationStrategy = validationStrategy;
    }

    public boolean validate(String s){
        return validationStrategy.execute(s);
    }
}
