package com.example.feign;

import com.example.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定义fallback类
 *
 * 需要将fallback类注册到spring容器中
 */
@Slf4j
@Component
public class ProviderFeignFallback implements ProviderFeign{
    @Override
    public UserInfo getUserInfo(Long id, String token) {
        log.info("fallback getUserInfo");
        return null;
    }
}
