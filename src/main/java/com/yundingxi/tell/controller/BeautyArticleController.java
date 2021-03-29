package com.yundingxi.tell.controller;
import com.yundingxi.tell.service.BeautyArticleService;
import com.yundingxi.tell.utils.Result;
import com.yundingxi.tell.utils.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/beauty_article")
@Api(value = "/beauty_article" ,tags = "美文接口")
public class BeautyArticleController {
    @Resource
    private BeautyArticleService beautyArticleService;

    /**
     * 爬取美食网站信息集合
     *
     * @return 美食信息集合
     */

    @ResponseBody
    @Operation(description = "爬取美文")
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
    @Operation(description = "定时更新美文")
    @Scheduled(cron = "${time.cron}")
    @ResponseBody
    @GetMapping("/update_beauty_article")
    public Result<String> updateBeautyArticle() {
        return beautyArticleService.updateBeautyArticle();
    }
    @Operation(description = "根据文章index获取美文详细内容")
    @ResponseBody
    @GetMapping("get_beauty_article")
    public Result<Object> getBeautyArticle(@Parameter(description = "文章的索引") @RequestParam String index) {
        Map<String, String> o = beautyArticleService.getBeautyArticle(index);
        return ResultGenerator.genSuccessResult(o);
    }
    @Operation(description = "获取美文显示页面信息")
    @GetMapping("/getBeautyArticleVo")
    @ResponseBody
    Result<Object> getBeautyArticleVo(){
        return ResultGenerator.genSuccessResult(beautyArticleService.getBeautyArticleVo());
    }
    @Operation(description = "插入美文首页图片")
    @GetMapping("/addBeautyArticleImg")
    @ResponseBody
    Result<Object> addBeautyArticleImg(@Parameter(description = "图片url")@RequestParam(required = false, defaultValue = "") String url){
        return beautyArticleService.addBeautyArticleImg(url);
    }

}