package com.yundingxi.tell.service.Impl;

import java.util.*;

import com.yundingxi.tell.common.enums.FileEnums;
import com.yundingxi.tell.common.enums.RedisEnums;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.util.ReptileUtils;
import com.yundingxi.tell.service.BeautyArticleService;
import com.yundingxi.tell.util.FileUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-11:21
 */
@Service
@Slf4j
public class BeautyArticleServiceImpl implements BeautyArticleService {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public Result<String> saveCrawlObject(int head,int end) {
        List<Object> list = new ArrayList<>();
        List<Map<String, Object>> lis = ReptileUtils.getElements(head,end);
        int num = 0;
        for (Map<String, Object> li : lis) {
            num++;
            if (num <= 3) {
                redisUtil.select(RedisEnums.SYS_BEAUTYWEN_HASHCODE.getRedisDbIndex());
                if (redisUtil.sSet(RedisEnums.SYS_BEAUTYWEN_HASHCODE.getRedisKey(), li.hashCode()) > 0) {
                    log.info("===================>>>  文章爬取成功  ");
                    list.add(li);
                } else {
                    log.info("===================>>>  本文已经被爬取过 ");
                    num--;
                }
            } else {
                long l = System.currentTimeMillis();
                System.out.println(l);
                boolean b = FileUtil.writeFile(FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getFilePath() + l + ".txt", list);
                if (b) {
                    list = new ArrayList<>();
                    log.info("对象以序列化成功   {}  文件名path ：{}", FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getDescription(), FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getFilePath() + l + ".tex");
                    num = 0;
                } else {
                    new Exception("对象序列化异常");
                }

            }
        }
        log.info("=================>对象序列化成功！！！！！！");
        return ResultGenerator.genSuccessResult("序列化成功！！！！！");
    }

    @Override
    public Result<String> updateBeautyArticle() {
        List<File> files = null;
        try {
            files = FileUtil.listFiles(FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getFilePath());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("=============> IO异常 {}", e.getMessage());
            return ResultGenerator.genFailResult("更新失败");
        }
        if(files.size()<=2||files==null){
            redisUtil.incr(RedisEnums.SYS_PMW_INDEX.getRedisKey(),2);
            Integer o = (Integer) redisUtil.get(RedisEnums.SYS_PMW_INDEX.getRedisKey());
            saveCrawlObject(o,o+2);
        }
        if (files.isEmpty()) {
            try {
                files = FileUtil.listFiles(FileEnums.SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH.getFilePath());
            } catch (IOException e) {
                e.printStackTrace();
                log.info("更新异常 ,异常原因:{}",e.getMessage());
                return ResultGenerator.genFailResult("更新失败");
            }
        }
        int random = (int) (Math.random() * files.size());
        File file = files.get(random);
        List<Map<String, String>> maps = (List<Map<String, String>>) FileUtil.redaFile(file);
        ArrayList<Map<String, String>> maps1 = new ArrayList<>();
        redisUtil.select(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisDbIndex());
        for (int i = 0; i < maps.size(); i++) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("index", i+"");
            stringStringHashMap.put("articleTitle",maps.get(i).get("articleTitle"));
            stringStringHashMap.put("articleTime", maps.get(i).get("articleTime"));          //获取url 控制主键值
            int num = (Integer) redisUtil.get(RedisEnums.SYS_BEAUTYWEN_HOME_IMG_URL_INDEX.getRedisKey());
            //随机生成图片
            int index= (int)(Math.random()*num+1);
            //在redis中 获取图片 url
            String imgUrl = (String) redisUtil.hget(RedisEnums.SYS_BEAUTYWEN_HOME_IMG_URL.getRedisKey(), index+"");
            maps.get(i).put("img_url",imgUrl);
            stringStringHashMap.put("img_url",imgUrl);
            //添加到list集合中
            maps1.add(stringStringHashMap);

            boolean set = redisUtil.set(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisKey() + ":" + i, maps.get(i));
            if (!set) {
                log.info("美文数据更新失败 时间：{} redis 异常 ", new Date());
                return ResultGenerator.genFailResult("更新失败");
            }
        }
        boolean set1 = redisUtil.set(RedisEnums.SYS_BEAUTYWEN_HOME_JSONS.getRedisKey(),maps1);
        if (!set1) {
            log.info("美文首页数据更新失败 时间：{} redis 异常 ", new Date());
            return ResultGenerator.genFailResult("更新失败");
        }
        try {
            log.info("美文数据更新成功 时间：{} 美文文件：{} ", new Date(), file.getName());
            FileUtil.moveFile(file.toString(), FileEnums.SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH.getFilePath());
            return ResultGenerator.genSuccessResult("更新成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("=============> IO异常 {}", e.getMessage());
            return ResultGenerator.genFailResult(file.getName() + "文件移除失败，请检查");
        }
    }

    @Override
    public Map<String, String> getBeautyArticle(String id) {
        redisUtil.select(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisDbIndex());
        Map<String, String> o = (Map<String, String>) redisUtil.get(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisKey() + ":"+id);
        return o;
    }

    @Override
    public List<Map<String, String>> getBeautyArticleVo() {
        redisUtil.select(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisDbIndex());
        List<Map<String, String>> data = (List<Map<String, String>>) redisUtil.get(RedisEnums.SYS_BEAUTYWEN_HOME_JSONS.getRedisKey());
        return data;
    }

    @Override
    public Result<Object> addBeautyArticleImg(String url) {
            redisUtil.hset(RedisEnums.SYS_BEAUTYWEN_HOME_IMG_URL.getRedisKey(),redisUtil.incr(RedisEnums.SYS_BEAUTYWEN_HOME_IMG_URL_INDEX.getRedisKey(),1)+"",url);
        return ResultGenerator.genSuccessResult(redisUtil.hmget(RedisEnums.SYS_BEAUTYWEN_HOME_IMG_URL.getRedisKey()));
    }
}
