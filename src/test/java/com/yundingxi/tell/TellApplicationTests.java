package com.yundingxi.tell;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;

@SpringBootTest
class TellApplicationTests {
    @Autowired
    private LetterMapper letterMapper;
    @Autowired
    private UserMapper userMapperr;
    @Autowired
    private UserService userService;
    @Test
    void contextLoads() {

    }
}
