package com.yundingxi.tell.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version v1.0
 * @ClassName AchieveMapper
 * @Author rayss
 * @Datetime 2021/5/13 10:45 上午
 */

@Mapper
public interface AchieveMapper {

    List<String> selectAllAchieveType();

}
