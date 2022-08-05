package com.client.redis;

import com.client.model.domain.Users;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @ClassName: RedisTest
 * @author: mafangnian
 * @date: 2023/5/25 16:59
 */

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("02String", "fish");
        valueOperations.set("02Int", 1);
        valueOperations.set("02Double", 2.0);
        Users user = new Users();
        user.setId(1L);
        user.setUsername("02");
        valueOperations.set("02User", user);

        // 查
        Object ac = valueOperations.get("02String");
        Assertions.assertTrue("fish".equals((String) ac));
        ac = valueOperations.get("02Int");
        Assertions.assertTrue(1 == (Integer) ac);
        ac = valueOperations.get("02Double");
        Assertions.assertTrue(2.0 == (Double) ac);
        System.out.println(valueOperations.get("02User"));
    }
}
