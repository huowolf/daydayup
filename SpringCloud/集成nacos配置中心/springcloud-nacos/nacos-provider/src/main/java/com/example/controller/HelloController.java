package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class HelloController {


    @Value("${disableCouponRequest:false}")
    private Boolean disableCoupon;

    @GetMapping("/hello")
    public String hello(){
        return "hello,nacos!";
    }

    @GetMapping("/getVal")
    public String getVal(){
        return "disableCoupon:"+disableCoupon;
    }
}
