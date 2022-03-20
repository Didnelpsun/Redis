// RedisUtils.java
package com.didnelpsun.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

public class RedisUtil {
    // 产品库存后缀
    public static final String stockSuffix = "s";
    // 秒杀清单后缀
    public static final String killSuffix = "k";
    // 秒杀
    public static Boolean secKill(String userId, String productId){
        // 首先对userId和productId判空
        if(userId.length() == 0 || productId.length() == 0){
            System.out.println("输入参数为空");
            return false;
        }
        // 对userId和productId是否有效进行判断，使用SQL数据库，这里略过
        // 连接Redis
//        Jedis jedis = new Jedis("127.0.0.1",6379);
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
        // 由于库存可能会出现混乱，所以对库存监视
        jedis.watch(productId + stockSuffix);
        // 获取product库存，如果库存为null则表示秒杀未开始，如果库存小于1则表示已经没有库存，返回false
        String stock = jedis.get(productId + stockSuffix);
        if (stock == null){
            System.out.println("秒杀未开始");
            jedis.close();
            return false;
        }
        if(Integer.parseInt(stock)<1){
            System.out.println("库存不足");
            jedis.close();
            return false;
        }
        // 根据秒杀清单查看用户是否在里面，如果在就返回false不允许再次秒杀
        // 秒杀清单应该为一个集合
        if(jedis.sismember(productId+killSuffix,userId)){
            System.out.println("已经秒杀成功");
            jedis.close();
            return false;
        }
        // 如果秒杀成功，则库存-1，秒杀成功的userId和productId添加到数据库中
//        jedis.decr(productId+stockSuffix);
//        jedis.sadd(productId+killSuffix,userId);
        // 开启事务
        Transaction multi = jedis.multi();
        // 组队
        multi.decr(productId+stockSuffix);
        multi.sadd(productId+killSuffix,userId);
        // 执行
        List<Object> result = multi.exec();
        if(result == null || result.size()==0){
            System.out.println("秒杀失败，存在异常");
            jedis.close();
            return false;
        }
        System.out.println("秒杀成功");
        jedis.close();
        return true;
    }
}
