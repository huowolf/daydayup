package com.huowolf.template;

import java.util.function.Consumer;

/**
 * 使用lambda改善模板方法模式
 */
public class OnlineBankingProve {

    public static class Customer {
        public Integer id;
        public String name;

        public Customer(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Database {
        public static Customer getCustomerWithId(int id){
            return new Customer(id,"超级VIP");
        }
    }

    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy){
        Customer customer = Database.getCustomerWithId(id);
        makeCustomerHappy.accept(customer);
    }

}
