package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.vo.AchieveVo;
import com.yundingxi.tell.bean.entity.UserAchieve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;



@Mapper
public interface AchieveMapper {
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

    List<String> selectAllAchieveType();

    List<String> selectAllTaskIdByAchieveTypeAndLocation(@Param("locationObtained") int locationObtained,@Param("achieveType") String achieveType);

    List<Achieve> selectAllTaskIdAndIdByAchieveTypeAndLocation(@Param("locationObtained") int locationObtained,@Param("achieveType") String achieveType);

    String selectAchieveRewardById(@Param("id") String id);

    int insertSingleNewUserAchieve(@Param("userAchieve") UserAchieve userAchieve);

    List<Achieve> selectAllAchieve();

    List<Achieve> selectAllTaskIdAndIdByAchieveTypeAndNonId(@Param("list") ArrayList<String> list, @Param("achieveType") String achieveType);
}
