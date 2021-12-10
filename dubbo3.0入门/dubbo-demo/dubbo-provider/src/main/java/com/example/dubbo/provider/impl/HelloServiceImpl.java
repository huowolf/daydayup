package com.example.dubbo.provider.impl;


import com.example.dubbo.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
