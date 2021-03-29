package com.yundingxi.tell.service.Impl;
import	java.util.Date;
import com.yundingxi.tell.common.enums.FileEnums;
import com.yundingxi.tell.common.enums.RedisEnums;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.util.ReptileUtils;
import com.yundingxi.tell.service.BeautyArticleService;
import com.yundingxi.tell.util.FileUtil;
import com.yundingxi.tell.utils.Result;
import com.yundingxi.tell.utils.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private  RedisUtil redisUtil;

    @Override
    public Result<String> saveCrawlObject() {
        List<Object> list = new ArrayList<>();
        List<Map<String, Object>> lis = ReptileUtils.getElements();
        int num=0;
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
                    log.info("对象以序列化成功   {}  文件名path ：{}", FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getDescription(),FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getFilePath() +l+".tex");
                    num = 0;
                } else {
                    new Exception("对象序列化异常");
                }

            }
        }
        log.info("=================>对象序列化成功！！！！！！");
        return ResultGenerator.genFailResult("序列化成功！！！！！");
    }

    @Override
    public Result<String> updateBeautyArticle() {
        try {
            List<File> files = FileUtil.listFiles(FileEnums.SYS_BEAUTY_ARTICLE_FILE_PATH.getFilePath());
            if (files.isEmpty()) {
                return ResultGenerator.genFailResult("数据源空 null 更新失败");
            }
            int random = (int) (Math.random() * files.size());
            File file = files.get(random);
            List<Map<String, String>> maps = (List<Map<String, String>>) FileUtil.redaFile(file);
            redisUtil.select(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisDbIndex());
            boolean set = redisUtil.set(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisKey(), maps);
            if (set) {
                FileUtil.moveFile(file.toString(),FileEnums.SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH.getFilePath());
                log.info("美文数据更新成功 时间：{} 美文文件：{} ",new Date(), file.getName());
                return ResultGenerator.genSuccessResult("更新成功");
            } else {
                log.info("美文数据更新失败 时间：{} redis 异常 ",new Date());
                return ResultGenerator.genFailResult("更新失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
              log.info("=============> IO异常 {}",e.getMessage());
            return ResultGenerator.genFailResult("更新失败");
        }
    }

    @Override
    public List<Map<String, String>> getBeautyArticle() {
        redisUtil.select(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisDbIndex());
        List<Map<String, String>> o = (List<Map<String, String>>) redisUtil.get(RedisEnums.SYS_BEAUTYWEN_JSONS.getRedisKey());
        return o;
    }
}
