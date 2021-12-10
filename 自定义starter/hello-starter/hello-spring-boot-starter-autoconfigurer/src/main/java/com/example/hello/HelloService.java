package com.example.hello;


public class HelloService {

    HelloProperties helloProperties;

    public HelloService(HelloProperties helloProperties){
        this.helloProperties = helloProperties;
    }

    public String sayHello(String name){
        return helloProperties.getPrefix()+"-"+name+"-"+helloProperties.getSuffix();
    }
}
