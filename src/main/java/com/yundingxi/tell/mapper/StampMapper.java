package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.UserStamp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version v1.0
 * @ClassName StampMapper
 * @Author rayss
 * @Datetime 2021/5/13 11:27 下午
 */

@Mapper
public interface StampMapper {
    int insertSingleNewUserStamp(@Param("userStamp") UserStamp userStamp);
}
