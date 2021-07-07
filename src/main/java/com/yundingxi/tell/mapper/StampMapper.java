package com.yundingxi.tell.mapper;


import com.yundingxi.tell.bean.entity.Stamp;
import com.yundingxi.tell.bean.entity.UserStamp;
import com.yundingxi.tell.bean.vo.StampVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version v1.0
 * @ClassName StampMapper
 * @Author rayss
 * @Datetime 2021/5/13 11:27 下午
 */

@Mapper
public interface StampMapper {
    /**
     * 获取自己获取的邮票
     * @return 邮票集合
     * @param openId
     */
    List<StampVo> haveListMeAll(@Param("openId") String openId);

    /**
     * 获取用户未获得的邮票集合
     * @param openId 用户的opemId
     * @return 返回邮票集合
     */
    List<StampVo> notHaveListMeAll(@Param("openId") String openId);

    int insertSingleNewUserStamp(@Param("userStamp") UserStamp userStamp);

    List<Stamp> selectAllStamp();

    List<UserStamp> selectBaseStamp();
}
