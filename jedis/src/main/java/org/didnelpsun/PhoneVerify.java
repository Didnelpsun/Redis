// PhoneVerify.java
package org.didnelpsun;

import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.Random;

public class PhoneVerify {
    // 主机
    public static final String host = "127.0.0.1";
    // 端口
    public static final int port = 6379;
    // 次数限制时间
    public static final int limit = 24*60*60;
    // 验证码有效时间
    public static final int verify = 120;
    // 有效次数的key的后缀
    public static final String eff = "e";
    // 验证码的key的后缀
    public static final String ver = "v";
    // 生成六位随机数字
    public static String getRandomCode(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for(int i=0;i<6;i++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    // 发送并保存验证码
    public static String setRandomCode(String telephone){
        // 连接Redis
        Jedis jedis = new Jedis(host,port);
        // 由于有效时间只对每个键生效，而我们需要两个有效期，所以需要两个键
        // 有效次数的key为telephone+e
        String e = telephone + eff;
        // 验证码的key为telephone+v
        String v = telephone + ver;
        // 先检查telephone是否有多余的有效次数
        // 判断是否是第一次请求，如果是就进行初始化
        if(!jedis.exists(e)){
            jedis.setex(e,limit,"3");
        }
        // 对telephone+e值减一，表示当前之后还可以发几次
        jedis.decr(e);
        if(Integer.parseInt(jedis.get(e))<0){
            jedis.close();
            return "NULL";
        }
        // 如果还可以发送就随机生成一个code并保存到Redis中
        String code = getRandomCode();
        // 如果是第一次就是新增，如果不是就是更新
        jedis.setex(v,verify,code);
        jedis.close();
        return code;
    }

    // 对手机号和验证码校验
    public static Boolean verifyTelephone(String telephone, String code){
        return Objects.equals(new Jedis(host,port).get(telephone + ver), code);
    }
}
