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

    List<Letter> selectLetterLimit(@Param("letterCountLocation") int letterCountLocation);

    void insertReply(@Param("reply") Reply reply);

    void updateLetterTap(@Param("tabId") String tabId);

    Letter selectLetterById(String letterId);

    String selectPenNameByOpenId(@Param("openId") String openId);

    String selectContentByLetterId(@Param("letterId") String letterId);
}
