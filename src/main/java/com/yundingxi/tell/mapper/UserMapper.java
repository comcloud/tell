package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.UserCommentVo;
import com.yundingxi.tell.bean.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    Integer insertUser(@Param("user") User user);

    /**
     */
    List<String> selectAllOpenId();


    List<UserCommentVo> getUserCommentVos(@Param("openId") String openId);

    UserVo getUserVoById(@Param("openId")String openId);

    String selectPenNameByOpenId(@Param("openId") String openId);
}
