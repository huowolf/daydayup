package com.huowolf.template;

public class Main {

    public static void main(String[] args) {
        OnlineBankingProve onlineBankingProve = new OnlineBankingProve();
        onlineBankingProve.processCustomer(111, (customer) -> System.out.println("Hello,"+customer.name));
    }
}
