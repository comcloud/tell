package com.yundingxi.tell;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.Impl.UserServiceImpl;
import com.yundingxi.tell.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
        userService.insertUser(new User("vx0012",new Date(),1,new Date(),new Date(),0,"幸福的小洞洞","aaa"));
    }

    @Test
    void fileUtilTest() {
//        String md5ByFile = FileUtil.getMd5ByFile(new File("D:\\numer1\\tell\\a\\1616581236597.txt"));
//        String md5ByFile2 = FileUtil.getMd5ByFile(new File("D:\\numer1\\tell\\b\\1616581244627.txt"));
//        System.out.println(md5ByFile.equals(md5ByFile2));

//        ArrayList<Object> objects = new ArrayList<>();
//        objects.add('a');
//        ArrayList<Object> objects2 = new ArrayList<>();
//        objects2.add(97);
//        System.out.println(objects.equals(objects2));
//        System.out.println(objects.hashCode());
//        System.out.println(objects2.hashCode());
//
//        System.out.println("A".hashCode());
//        userMapper.insertUser(new User());
//        userMapperr.insertUser();
    }

}
