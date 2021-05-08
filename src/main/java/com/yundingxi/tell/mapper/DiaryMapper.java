package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Diarys;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version v1.0
 * @ClassName DiaryMapper
 * @Author rayss
 * @Datetime 2021/3/30 10:43 上午
 */

@Mapper
public interface DiaryMapper {
    void insertSingleDiary(@Param("diarys") Diarys diarys);

    void deleteSingleDiaryById(@Param("id") String id);

    List<Diarys> selectAllDiaryByOpenIdAndNonState(@Param("openId") String openId, @Param("state") String state);

    Diarys selectSingleDiary(@Param("id") String id);

    List<Diarys> selectAllPublicDiary(@Param("state") String state);

    void updateDiaryNumber(@Param("id") String id, @Param("viewNum") Integer viewNum);

    List<String> selectAllDiaryContentByOpenId(@Param("openId") String openId,@Param("currentTime") String currentTime);

    int updateDiaryState(@Param("id") String id, @Param("state") int state);
}
