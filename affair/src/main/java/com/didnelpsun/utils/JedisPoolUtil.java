// JedisPoolUtil.java
package com.didnelpsun.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

// Redis连接池
public class JedisPoolUtil {
    private static volatile JedisPool jedisPool = null;
    public static String host = "127.0.0.1";
    public static int port = 6379;
    public static int timeout = 60000;
    private JedisPoolUtil(){

    }
    public static JedisPool getJedisPoolInstance(){
        if(jedisPool == null){
            synchronized (JedisPoolUtil.class){
                if(jedisPool == null){
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    // 最大连接数
                    poolConfig.setMaxTotal(200);
                    // 最大闲置连接
                    poolConfig.setMaxIdle(32);
                    // 最大等待时间
                    poolConfig.setMaxWait(Duration.ofSeconds(10));
                    // 超过连接数是否阻塞
                    poolConfig.setBlockWhenExhausted(true);
                    // 连接时是否测试连接
                    poolConfig.setTestOnBorrow(true);
                    jedisPool = new JedisPool(poolConfig, host, port, timeout);
                }
            }
        }
        return jedisPool;
    }

    public static void release(JedisPool jedisPool, Jedis jedis){
        if(jedis != null){
            jedisPool.returnResource(jedis);
        }
    }
}
