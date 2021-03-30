package com.yundingxi.tell.service;

import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.entity.Diarys;

import java.util.List;

/**
 * @version v1.0
 * @ClassName DiaryService
 * @Author rayss
 * @Datetime 2021/3/30 10:41 上午
 */

public interface DiaryService {
    /**
     * 保存用户的日记对象
     * @param diaryDto 一个日记
     */
    void saveDiary(DiaryDto diaryDto);

    void removeDiaryById(String id);

    List<Diarys> getAllDiaryForSelfByOpenId(String openId);

    Diarys getDetailById(String id);
}
