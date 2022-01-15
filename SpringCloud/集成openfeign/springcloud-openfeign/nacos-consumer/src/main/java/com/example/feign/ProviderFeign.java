package com.example.feign;

import com.example.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * value: 服务注册名称
 * path: 指定前置访问路径
 * fallback: 指定降级类
 */
@FeignClient(value = "nacos-provider",path = "/user",fallback = ProviderFeignFallback.class)
public interface ProviderFeign {

    @GetMapping("/test")
    UserInfo getUserInfo(@RequestParam Long id, @RequestHeader(required = false) String token);
}
