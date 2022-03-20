// TestController
package com.didnelpsun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @GetMapping("/test")
    public String testRedis(){
        redisTemplate.opsForValue().set("test", "test");
        return (String) redisTemplate.opsForValue().get("test");
    }
}
