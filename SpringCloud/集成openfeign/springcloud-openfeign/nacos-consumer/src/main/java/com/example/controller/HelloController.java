package com.example.controller;

import com.example.dto.UserInfo;
import com.example.feign.ProviderFeign;
import feign.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private ProviderFeign providerFeign;

    @GetMapping("/user/info")
    public UserInfo getUserInfo(@RequestParam Long id, @RequestHeader(required = false) String token){
        return providerFeign.getUserInfo(id,token);
    }
}
