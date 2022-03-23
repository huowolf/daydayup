package com.huowolf.strategy;

/**
 * 用lambda改善策略模式
 */
public class Main {

    public static void main(String[] args) {
//        Validator numericValidator = new Validator(new IsNumber());
//        boolean b1 = numericValidator.validate("aaaa");
//        System.out.println(b1);
//        Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
//        boolean b2 = lowerCaseValidator.validate("bbbb");
//        System.out.println(b2);

        Validator numericValidator = new Validator(s -> s.matches("\\d+"));
        boolean b1 = numericValidator.validate("aaaa");
        System.out.println(b1);

        Validator lowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));
        boolean b2 = lowerCaseValidator.validate("aaaa");
        System.out.println(b2);
    }
}
