package com.yundingxi.tell.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version v1.0
 * @ClassName UserMapper
 * @Author rayss
 * @Datetime 2021/3/25 9:02 下午
 */

@Mapper
public interface UserMapper {
    List<String> selectAllOpenId();
}
