package com.sdisk;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: springboot-redis
 * @description:
 * @author: Mr.Huang
 * @create: 2020-06-19 16:57
 **/
public class LettuceTest extends LettuceRedisApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(LettuceTest.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test01(){
        redisTemplate.opsForValue().set("333", "sdisk");
        String str = (String) redisTemplate.opsForValue().get("333");
        log.info("333 =>{}", str);
    }
}
