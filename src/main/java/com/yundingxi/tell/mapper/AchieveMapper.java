package com.yundingxi.tell.mapper;


import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.entity.Task;
import com.yundingxi.tell.bean.vo.AchieveVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Mapper
public interface AchieveMapper{
    /**
     * 获取自己获取的邮票
     * @return 邮票集合
     * @param openId
     */
    List<AchieveVo> haveListMeAll(@Param("openId") String openId);

    /**
     * 获取用户未获得的邮票集合
     * @param openId 用户的opemId
     * @return 返回邮票集合
     */
    List<AchieveVo> notHaveListMeAll(@Param("openId") String openId);
}
