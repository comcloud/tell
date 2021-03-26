package com.yundingxi.tell.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.jni.User;

import java.util.List;

/**
 * @version v1.0
 * @ClassName UserMapper
 * @Author rayss
 * @Datetime 2021/3/25 9:02 下午
 */

@Mapper
public interface UserMapper {
    /**
     *  插入用户信息
     * @param user 用户类
     * @return
     */
    Integer insertUser(@Param("user")User user);
}
