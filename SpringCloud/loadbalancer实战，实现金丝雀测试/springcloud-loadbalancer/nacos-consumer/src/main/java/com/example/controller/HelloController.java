package com.example.controller;

import com.example.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class HelloController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/hello")
    public String hello(){
        String result = webClientBuilder.build()
                .get()
                .uri("http://nacos-provider/hello")
                .header(Constant.TRAFFIC_VERSION,"TRAFFIC_VERSION_01")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return result;
    }
}
