package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version v1.0
 * @ClassName LetterMapper
 * @Author rayss
 * @Datetime 2021/3/24 6:44 下午
 */

@Mapper
public interface LetterMapper {
    int insertSingleLetter(@Param("letter") Letter letter);

    List<Letter> selectLetterLimit(@Param("letterCountLocation") int letterCountLocation
            , @Param("openId") String openId);


    void insertReply(@Param("reply") Reply reply);

    void updateLetterTap(@Param("tabId") String tabId);

    Letter selectLetterById(String letterId);

    String selectPenNameByOpenId(@Param("openId") String openId);

    String selectContentByLetterId(@Param("letterId") String letterId);

    /**
     * @param openId   open id
     * @param period   时间间隔，表示多少个时间段，比如0～7，7 ～ 14
     * @param interval 选用的时间段为多少，就是上面的7，7表示一周，给7的话代表获取某一周，具体哪一周由period参数决定
     * @param currentTime 指定的当前时间
     * @return 数量
     */
    Integer selectWeeklyQuantityThroughOpenId(@Param("openId") String openId, @Param("currentTime") String currentTime, @Param("tableName") String tableName, @Param("period") Integer period, @Param("interval") Integer interval);

    List<String> selectAllLetterContentByOpenId(@Param("openId") String openId,@Param("currentTime") String currentTime);

    String selectPenNameById(@Param("letterId") String letterId);

    List<Letter> selectAllLetterByOpenId(@Param("openId") String openId);

    int updateLetterState(@Param("id") String id, @Param("state") int state);
}
