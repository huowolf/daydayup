package com.huowolf.template;

public abstract class OnlineBanking {
    public static class Customer {}

    public static class Database {
        public static Customer getCustomerWithId(int id){
            return new Customer();
        }
    }

    public void processCustomer(int id){
        Customer customer = Database.getCustomerWithId(id);
        makeCustomerHappy();
    }

    abstract void makeCustomerHappy();
}
