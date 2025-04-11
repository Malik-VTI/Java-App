package com.project.ecommerceapp.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/test")
public class TestController {
    private final StringRedisTemplate redisTemplate;

    public TestController (StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis")
    public String testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        return redisTemplate.opsForValue().get("testKey");
    }
}
