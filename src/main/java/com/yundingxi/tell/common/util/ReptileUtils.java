package com.yundingxi.tell.common.util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/25-20:14
 */
public class ReptileUtils {
    private static final String URL="http://www.meiwenjx.com/lizhimeiwen/lizhimingyan/list_";
    private static final int TIMEOUT_MILLIS =10000;
    private static final int NUMBER=1;
    private static final int TIMEOUT=2;
    public static  List<Map<String, Object>> getElements(){
        Elements lis = null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= NUMBER; i++) {
            String url =URL+ i + ".html";
            Document document = null;
            try {
                document = Jsoup.parse(new URL(url), TIMEOUT_MILLIS);
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(TIMEOUT);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    document = Jsoup.parse(new URL(url), TIMEOUT_MILLIS);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            Elements content = document.getElementsByClass("listitem");
            lis = content.select(".e2>li");
        }
        // 建立集合存储多个键值对
        for (Element li : lis) {
            // 获取对应文章链接
            String titleUrl = li.select("a").attr("href");
            // 文章具体地址
            String articleUrl = "http://www.meiwenjx.com" + titleUrl;
            Document doc = null;
            try {
                doc = Jsoup.parse(new URL(articleUrl), 10000);
            } catch (Exception e) {
                continue;
            }
            // 文章标题
            String articleTitle = doc.select(".viewbox>.title>h1").text();
            // 文章时间
            String articleText = doc.select("#info").text();
            int index = articleText.indexOf("时间") + 3;
            String articleTime = articleText.substring(index > 0 ? index : 0, index > 0 ? index + 16 : 0);
            articleTime = articleTime.equals("") ? "2020-12-01 10:46" : articleTime;
            // 文章内容
            String articleContent = doc.select("#content>p").text();
            // 文章标签
            String articleTag = doc.select(".place>a").get(2).text();
            // 建立map集合
            HashMap<String, Object> map = new HashMap<>();
            // 添加键值对
            map.put("articleTitle", articleTitle);
            map.put("articleTime", articleTime);
            map.put("articleContent", articleContent);
            map.put("articleTag", articleTag);
            list.add(map);
        }
        return list;
    }
}
