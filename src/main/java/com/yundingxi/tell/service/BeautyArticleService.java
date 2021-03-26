package com.yundingxi.tell.service;


import com.yundingxi.tell.utils.Result;

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
    Result<String> saveCrawlObject();

    /**
     * 更新美文数据
     * @return
     */
    Result<String> updateBeautyArticle();

    List<Map<String, String>> getBeautyArticle();
}