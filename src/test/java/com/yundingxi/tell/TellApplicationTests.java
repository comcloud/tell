//package com.yundingxi.tell;
//import com.yundingxi.tell.common.redis.RedisUtil;
//import com.yundingxi.tell.config.RedisConfig;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@SpringBootTest
//class TellApplicationTests {
//
////    @Test
////    void contextLoads() {
////    }
////    @Test
////    void fileUtilTest() throws IOException, NoSuchAlgorithmException {
//////        String md5ByFile = FileUtil.getMd5ByFile(new File("D:\\numer1\\tell\\a\\1616581236597.txt"));
//////        String md5ByFile2 = FileUtil.getMd5ByFile(new File("D:\\numer1\\tell\\b\\1616581244627.txt"));
//////        System.out.println(md5ByFile.equals(md5ByFile2));
////
////        ArrayList<Object> objects = new ArrayList<>();
////        objects.add('a');
////        ArrayList<Object> objects2 = new ArrayList<>();
////        objects2.add(97);
////        System.out.println(objects.equals(objects2));
////        System.out.println(objects.hashCode());
////        System.out.println(objects2.hashCode());
////
////        System.out.println("A".hashCode());
////    }
//@Autowired
//private RedisConfig redisConfig;
//@Autowired
//RedisUtil redis;
//@Autowired
//
//private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    void contextLoads() {
//        //切换到1库
//        redis.select(3);
//        System.out.println(redis.set("555", "666"));
//    }
//
//}
