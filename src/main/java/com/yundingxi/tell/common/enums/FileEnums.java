package com.yundingxi.tell.common.enums;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-14:12
 */
@AllArgsConstructor

public enum FileEnums implements FileBaseEnums{
    /**
     * 美文序列化保存文件夹
     */
//<<<<<<< HEAD
////<<<<<<< HEAD
//    SYS_BEAUTY_ARTICLE_FILE_PATH("src/main/resources/static/beautyArticle/","美文序列化对象保存文件夹"),
//    SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH("src/main/resources/static/delete/","美文序列化对象删除文件夹");
////=======
//<<<<<<< HEAD
//    SYS_BEAUTY_ARTICLE_FILE_PATH("D:\\YunDingXi\\tell\\src\\main\\resources\\static\\beautyArticle\\","美文序列化对象保存文件夹"),
//    SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH("D:\\YunDingXi\\tell\\src\\main\\resources\\static\\delete\\","美文序列化对象删除文件夹");
//=======
//    SYS_BEAUTY_ARTICLE_FILE_PATH("/home/tell/beautyArticle/","美文序列化对象保存文件夹"),
//    SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH("/home/tell/delete/","美文序列化对象删除文件夹");
    SYS_BEAUTY_ARTICLE_FILE_PATH("/root/tell/beautyArticle/","美文序列化对象保存文件夹"),
    SYS_BEAUTY_ARTICLE_FILE_DELETE_PATH("/root/tell/delete/","美文序列化对象删除文件夹");

    @Setter
    private String  filePath;
    @Setter
    private String description;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }
}
