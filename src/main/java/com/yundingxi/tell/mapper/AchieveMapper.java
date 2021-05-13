package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.entity.UserAchieve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<String> selectAllTaskIdByAchieveTypeAndLocation(@Param("locationObtained") int locationObtained,@Param("achieveType") String achieveType);

    List<Achieve> selectAllTaskIdAndIdByAchieveTypeAndLocation(@Param("locationObtained") int locationObtained,@Param("achieveType") String achieveType);

    String selectAchieveRewardById(@Param("id") String id);

    int insertSingleNewUserAchieve(@Param("userAchieve") UserAchieve userAchieve);
}
