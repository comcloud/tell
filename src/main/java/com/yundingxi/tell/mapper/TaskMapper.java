package com.yundingxi.tell.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version v1.0
 * @ClassName TaskMapper
 * @Author rayss
 * @Datetime 2021/5/13 5:35 下午
 */

@Mapper
public interface TaskMapper {
    String selectTaskJsonByTaskId(@Param("taskId") String taskId);
}
