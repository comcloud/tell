package com.yundingxi.dao.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Mapper
public interface TaskMapper{

    String selectTaskJsonByTaskId(@Param("taskId") String taskId);
}
