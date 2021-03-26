package com.yundingxi.tell.controller;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.service.BeautyArticleService;
import com.yundingxi.tell.utils.Result;
import com.yundingxi.tell.utils.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.*;
/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/24-14:27
 */
@Controller
public class BeautyArticleController {
    @Resource
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private BeautyArticleService beautyArticleService;

    /**
     * 爬取美食网站信息集合
     *
     * @return 美食信息集合
     */
    @ResponseBody
    @RequestMapping(value = "/getArtIndex", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Result<String> deliciousIndex() {
        Result<String> result = beautyArticleService.saveCrawlObject();
        return result;
    }

    /**
     * 定时任务，每天中午12点更新数据
     *
     * @return
     */
    @Scheduled(cron = "${time.cron}")
    @ResponseBody
    @GetMapping("/update_beauty_article")
    public Result<String> updateBeautyArticle() {
        return beautyArticleService.updateBeautyArticle();
    }

    @ResponseBody
    @GetMapping("get_beauty_article")
    public Result<Object> getBeautyArticle() {
        List<Map<String, String>> o = beautyArticleService.getBeautyArticle();
        return ResultGenerator.genSuccessResult(o);
    }
}