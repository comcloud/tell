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
     * @param entity 用户类
     * @return
     */
    Integer insertUser(@Param("entity") User entity);

    /**
     * 修改用户信息
     * @param entity User 用户实体类
     * @return
     */
    Integer updateUser(@Param("entity") User entity);

    /**
     * 用户退出时更新用户最初
     * @param openId  用户OPenID
     * @return
     */
    Integer updateOutDate(@Param("openId")String openId);


    /**
     *
     */
    List<String> selectAllOpenId();


    List<UserCommentVo> getUserCommentVos(@Param("openId") String openId);

    UserVo getUserVoById(@Param("openId")String openId);

    String getIDBySgId(@Param("sgId")String sgId);

    String selectPenNameByOpenId(@Param("openId") String openId);

    User selectNameAndUrlByOpenId(@Param("openId") String openId);
}
