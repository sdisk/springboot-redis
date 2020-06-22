package com.sdisk;

import com.sdisk.entity.UserEntity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * @description: jedis测试，先本地开启redis服务，运行redis-server
 * @author: Mr.Huang
 * @create: 2020-06-19 14:22
 **/
public class JedisTest extends JedisRedisApplicationTests{

    private static final Logger log = LoggerFactory.getLogger(JedisTest.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JedisConnectionFactory factory;

    @Test
    public void test01(){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(111L);
        userEntity.setUsername("sdisk");
        userEntity.setGender("man");

        /**
         * 使用redisTemplate和stringRedisTemplate来操作redis，从redis中可以获取到值
         * 可以看到两个值相等，但是通过工具查看redis中保存的数据可以看到区别：
         * sdisk   /    "sdisk"
         *  这里会多加上一个引号
         *   redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
         *   原因是这里使用了GenericJackson2JsonRedisSerializer来序列化value，它会自动为String类型的键和值添加双引号
         *   而StringRedisTemplate不会，因为它使用了RedisSerializer.string()
         *   	public StringRedisTemplate() {
         * 		    setKeySerializer(RedisSerializer.string());
         * 		    setValueSerializer(RedisSerializer.string());
         * 		    setHashKeySerializer(RedisSerializer.string());
         * 		    setHashValueSerializer(RedisSerializer.string());
         *        }
         *  这个string()方法返回的是StringRedisSerializer
         */
        stringRedisTemplate.opsForValue().set("111" , userEntity.getUsername());
        String name = stringRedisTemplate.opsForValue().get("111");
        log.info("stringRedisTemplate receive={}", name);

        redisTemplate.opsForValue().set("222" , userEntity.getUsername());
        String name2 =  (String) redisTemplate.opsForValue().get("222");
        log.info("redisTemplate receive={}", name2);
        Assert.assertEquals(name, name2);

    }

    @Test
    public void test02(){
        String prefix = "user:id:";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(111L);
        userEntity.setUsername("sdisk");
        userEntity.setGender("man");
        redisTemplate.opsForValue().set(prefix+userEntity.getId(), userEntity);

        UserEntity userEntity2 = (UserEntity)redisTemplate.opsForValue().get(prefix + userEntity.getId());
        log.info(userEntity2.getUsername());
    }

    /**
     * 测试Jedis连接池
     */
    @Test
    public void test03(){
        boolean usePool = factory.getUsePool();
        log.info("isUsePool={}", usePool);
        int number = 20;
        CountDownLatch latch = new CountDownLatch(number);
        IntStream.range(0,number).forEach((i)->{
            Thread t = new Thread(()->{
                RedisConnection connection = null;
                try {
                    connection = factory.getConnection();
                    log.info("connection={}", connection.toString());
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!connection.isClosed()){
                        //不关闭连接连接池中连接耗尽就一直阻塞 max-wait: -1
                        log.info("关闭连接");
                        connection.close();
                    }
                }
                latch.countDown();
            }, "sdisk-thread-"+i);

            t.start();
        });
        try {
            log.info("等待{}个线程执行完毕", number);
            latch.await();
            log.info("{}个线程已经执行完毕", number);
            log.info("继续执行主线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04(){
        RedisConnection connection = factory.getConnection();
        byte[] bytes = connection.stringCommands().get("111".getBytes());
        String str = new String(bytes);
        log.info(str);
    }

    @Test
    public void test05(){
        //hash类型
        String prefix = "user";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(111L);
        userEntity.setUsername("sdisk");
        userEntity.setGender("man");
        redisTemplate.opsForHash().put(prefix, "id", userEntity.getId());
        redisTemplate.opsForHash().put(prefix, "username", userEntity.getUsername());
        redisTemplate.opsForHash().put(prefix, "gender", userEntity.getGender());

        String gender = (String)redisTemplate.opsForHash().get(prefix, "gender");
        log.info("从redis中获取gender={}", gender);
    }
}
