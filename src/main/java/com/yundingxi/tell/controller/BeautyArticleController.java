package com.yundingxi.tell.controller;
import com.yundingxi.tell.common.enums.RedisEnums;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.utils.FileUtil;
import com.yundingxi.tell.utils.Result;
import com.yundingxi.tell.utils.ResultGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/24-14:27
 */
@Controller
public class BeautyArticleController {
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 爬取美食网站信息集合
     *
     * @return 美食信息集合
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/getArtIndex", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public  Result deliciousIndex(){
        int num = 0;
        List<Map> list = new ArrayList<>();
        for(int i = 1; i <=1;i++){
            String url = "http://www.meiwenjx.com/lizhimeiwen/lizhimingyan/list_"+i+".html";
            Document document = null;
            try {
                document = Jsoup.parse(new URL(url), 10000);
            }catch (Exception e){
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    document = Jsoup.parse(new URL(url), 10000);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    return ResultGenerator.genFailResult("爬取出错！！！请联系管理员");
                }
            }
            Elements content = document.getElementsByClass("listitem");
            Elements lis = content.select(".e2>li");

            // 建立集合存储多个键值对
            for(Element li : lis) {
                num++;
                // 获取对应文章链接
                String titleUrl = li.select("a").attr("href");
                // 文章具体地址
                String articleUrl = "http://www.meiwenjx.com" + titleUrl;
                Document doc = null;
                try {
                    doc = Jsoup.parse(new URL(articleUrl),10000);
                }catch (Exception e){
                    continue;
                }
                // 文章标题
                String articleTitle = doc.select(".viewbox>.title>h1").text();
                // 文章时间
                String articleText = doc.select("#info").text();
                int index = articleText.indexOf("时间") + 3;
                String articleTime = articleText.substring(index > 0 ? index : 0,index > 0 ? index + 16 : 0);
                articleTime = articleTime.equals("") ? "2020-12-01 10:46" : articleTime;
                // 文章内容
                String articleContent = doc.select("#content>p").text();
                // 文章标签
                String articleTag = doc.select(".place>a").get(2).text();
                // 建立map集合
                HashMap<String,Object> map = new HashMap<>();
                // 添加键值对
                map.put("articleTitle",articleTitle);
                map.put("articleTime",articleTime);
                map.put("articleContent",articleContent);
                map.put("articleTag",articleTag);
                if (num<=3){
                    redisUtil.select(RedisEnums.SYS_BEAUTYWEN_HASHCODE.getRedisDbIndex());
                    if (redisUtil.sSet(RedisEnums.SYS_BEAUTYWEN_HASHCODE.getRedisKey(),map.hashCode())>0){
                        System.out.println("============================已经爬一条数据===========================");
                        list.add(map);
                    }else {
                        System.out.println("==========================本文章已经爬取过==========================");
                        num--;
                    }
                }else {
                    long l = System.currentTimeMillis();
                    System.out.println(l);
                    boolean b = FileUtil.writeFile("D:\\numer1\\tell\\b\\" + l + ".txt", list);
                    if (b) {
                        list = new ArrayList<>();
                        num=0;
                    }else {
                        new Exception("对象序列化异常");
                        return ResultGenerator.genFailResult("对象序列化异常");
                    }

                }
            }
        }
        return ResultGenerator.genSuccessResult("爬取成功！！！");
    }
    @ResponseBody
    @GetMapping("/get")
    public Result get(){
        List<Map<String, String>> maps = FileUtil.redaFile("D:\\numer1\\tell\\a\\1616575698155.txt");
        return ResultGenerator.genSuccessResult(maps);
        }
    }
