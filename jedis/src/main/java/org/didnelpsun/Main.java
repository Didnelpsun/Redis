// Main.java
package org.didnelpsun;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static final String host = "127.0.0.1";
    public static final int port = 6379;
    // 创建Jedis对象
    public static Jedis j = new Jedis(host, port);
    public static void main(String[] args){
        // 使用ping测试
        s(j.ping());
    }
    // 将判断key是否存在变为静态方法
    public static boolean e(String name){
        return j.exists(name);
    }
    // 将打印方法改名
    public static void s(Object object){
        System.out.println(object);
    }
    // 操作键
    @Test
    public void testKey(){
        // 获取Redis中的所有key
        Set<String> keys = j.keys("*");
        for(String key : keys){
            s(key);
        }
        // 判断是否存在
        if(e("key")){
            // 获取存活时间
            s(j.ttl("key"));
        }
        else{
            // 设置键
            s(j.set("key", "test"));
        }
        // 获取键
        s(j.get("key"));
    }
    // 操作列表
    @Test
    public void testList(){
        // 如果不存在
        if(!e("list")){
            // 右边插入
            j.rpush("list","value1", "value2");
        }
        // 获取列表全部值
        List<String> list = j.lrange("list",0,-1);
        s(list);
    }
    // 操作集合
    @Test
    public void testSet(){
        // 如果有就删除值
        if(e("set")){
            j.srem("value1", "value2");
        }
        j.sadd("set","value1","value2");
        // 获取集合所有值
        Set<String> set = j.smembers("set");
        // 可以看出set集合是乱序的
        s(set);
    }
    // 操作哈希
    @Test
    public void testHash(){
        if(e("hash")){
            j.hdel("hash","name","age");
        }
        Map<String,String> map = new HashMap<>();
        map.put("name","金");
        map.put("age","22");
        // 添加多个域需要使用map
        j.hmset("hash",map);
        List<String> hash = j.hmget("hash","name","age");
        for(String h: hash){
            s(h);
        }
    }
    // 操作有序集合
    @Test
    public void testZset(){
        if(!e("zset")){
            j.zadd("zset", 99, "shanghai");
            j.zadd("zset", 80, "wuhan");
        }
        List<String> zset = j.zrange("zset", 0 ,-1);
        s(zset);
    }
}
