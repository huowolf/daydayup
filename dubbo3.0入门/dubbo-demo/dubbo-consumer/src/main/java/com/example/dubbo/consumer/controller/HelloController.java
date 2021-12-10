package com.example.dubbo.consumer.controller;

import com.example.dubbo.service.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date 2021/11/17
 * @Created by wangxw257@chinaunicom.cn
 */
@RestController
public class HelloController {
    @DubboReference
    private HelloService helloService;

    @RequestMapping("/hello")
    public String hello() {
        return helloService.sayHello("dubbo");
    }
}
