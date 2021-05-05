package com.yundingxi.tell.util;

import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.IndexLetterDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @version v1.0
 * @ClassName GeneralDataProcessUtil
 * @Author rayss
 * @Datetime 2021/5/5 9:38 下午
 */

public class GeneralDataProcessUtil {

    private static final UserMapper USER_MAPPER = (UserMapper) SpringUtil.getBean("userMapper");

    public static List<IndexLetterDto> configLetterDataFromList(List<Letter> letterList, String openId) {
        List<IndexLetterDto> letterDtoList = new ArrayList<>();
        letterList.forEach(letter -> {
            IndexLetterDto letterDto = new IndexLetterDto(letter.getContent().length() > 25 ? letter.getContent().substring(0, 25) : letter.getContent(), letter.getOpenId(), letter.getId(), letter.getPenName(), letter.getStampUrl(), USER_MAPPER.selectPenNameByOpenId(openId), letter.getReleaseTime());
            letterDtoList.add(letterDto);
        });
        return letterDtoList;
    }

    public static List<DiaryDto> configDiaryDataFromList(List<Diarys> diaryList) {
        List<DiaryDto> diaryDtoList = new ArrayList<>();
        diaryList.forEach(diary -> diaryDtoList.add(new DiaryDto(diary.getContent().length() > 25 ? diary.getContent().substring(0, 25) : diary.getContent(), diary.getPenName(), diary.getWeather(), diary.getOpenId(), diary.getState())));
        return diaryDtoList;
    }

    public static List<SpittingGroovesVo> configSpitDataFromList(List<SpittingGrooves> spittingGroovesList) {
        List<SpittingGroovesVo> spittingGroovesVoList = new ArrayList<>();
        spittingGroovesList.forEach(spit -> spittingGroovesVoList.add(new SpittingGroovesVo(spit.getId(), spit.getNumber(), spit.getTitle(), spit.getAvatarUrl(), spit.getPenName())));
        return spittingGroovesVoList;
    }
}
