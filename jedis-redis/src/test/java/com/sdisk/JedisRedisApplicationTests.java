package com.sdisk;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class JedisRedisApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(JedisRedisApplicationTests.class);

    @Before
    public void before(){
        log.info("begin test...........");
    }

    @After
    public void after(){
        log.info("end test...........");
    }

    @Test
    void contextLoads() {
    }

}
