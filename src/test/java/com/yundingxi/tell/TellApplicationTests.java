package com.yundingxi.tell;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class TellApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Test
    void contextLoads() {
        redisUtil.select(3);
        redisUtil.set("name", "1");
    }
}
