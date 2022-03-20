// TestController.java
package com.didnelpsun.controller;

import com.didnelpsun.utils.RedisUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
public class TestController {
    // POST操作
    @PostMapping("/buy")
    public Boolean buyPost(@RequestParam String userId, @RequestParam String productId){
        return RedisUtil.secKill(userId, productId);
    }
    // Get操作，将GET路径转为POST
    @GetMapping("/buy/{userId}/{productId}")
    public Boolean buyGet(@PathVariable String userId, @PathVariable String productId){
        return buyPost(userId, productId);
    }
    // 随机生成用户ID
    public static String getRandomUserId(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for(int i=0;i<4;i++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    // 随机用户秒杀
    @GetMapping("/testBuy")
    public Boolean testBuy(){
        return buyPost(getRandomUserId(), "1");
    }
}
