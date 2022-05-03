package com.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    @SentinelResource(value = "hello",
            blockHandler = "hello_block",
            fallback = "hello_fallback")
    public String hello(){
        return "Hello,sentinel!";
    }

    public String hello_block(){
        logger.info("hello_block");
        return "hello_block";
    }

    public String hello_fallback(){
        logger.info("hello_fallback");
        return "hello_fallback";
    }
}
