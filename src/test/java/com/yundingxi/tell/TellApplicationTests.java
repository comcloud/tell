package com.yundingxi.tell;
import	java.util.Date;

import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.service.SpittingGroovesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TellApplicationTests {
    @Autowired
    SpittingGroovesMapper s;
    @Autowired
    CommentsMapper a;
    @Autowired
    SpittingGroovesService service;
    @Test
    void contextLoads() {
        SpittingGrooves lllll = new SpittingGrooves("00001","...0000.",new Date(),"1","s","002","","","");
        service.insert(lllll);
//        s.insert(lllll);
//        s.updateById(lllll);
//            s.deleteById("001");
//        System.out.println(s.selectDetailsById("001"));
        System.out.println(a.selectAll("ab30fc9c-aabd-48f5-8e98-2a701052bffc"));

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
