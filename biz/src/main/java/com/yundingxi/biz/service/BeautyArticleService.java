package com.yundingxi.biz.service;


import com.yundingxi.common.util.response.Result;

import java.util.List;
import java.util.Map;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/24-14:27
 */
public interface BeautyArticleService {
    /**
     * 保存 获取的对象到指定文件目录
     */
    Result<String> saveCrawlObject(int index, int end);

    /**
     * 更新美文数据
     * @return
     */
    Result<String> updateBeautyArticle();

    Map<String, String> getBeautyArticle(String id);

    List<Map<String, String>> getBeautyArticleVo();

    Result<Object> addBeautyArticleImg(String url);
}