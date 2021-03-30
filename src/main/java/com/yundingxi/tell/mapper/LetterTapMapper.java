package com.yundingxi.tell.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/27-17:14
 */
@Mapper
public interface LetterTapMapper {
    List<String> getTapsID();

}
