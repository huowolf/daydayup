package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
public class HelloTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testHelloPredicates() {
        webClient.get()
                .uri("/hello/str")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // 验证状态
                .expectStatus().isOk()
                // 验证结果，注意结果是字符串格式
                .expectBody(String.class).consumeWith(result  -> assertTrue(result.getResponseBody().contains(Constants.HELLO_PREFIX)));
    }


    @Test
    void testLoadBalance() {
        webClient.get()
                .uri("/lb/str")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // 验证状态
                .expectStatus().isOk()
                // 验证结果，注意结果是字符串格式
                .expectBody(String.class).consumeWith(result  -> assertTrue(result.getResponseBody().contains(Constants.LB_PREFIX)));
    }
}

