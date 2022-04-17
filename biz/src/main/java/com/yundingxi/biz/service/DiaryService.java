package com.yundingxi.web.biz.service;


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
     * @return
     */
    int saveDiary(DiaryDto diaryDto);

    void removeDiaryById(String id);

    List<Diarys> getAllDiaryForSelfByOpenId(String openId);

    Diarys getDetailById(String id);

    List<Diarys> getAllPublicDiary();

    PageInfo<Diarys> getAllPublicDiary(Integer pageNum);

    void setViews(DiaryViewDto[] viewDtoList);

    void setViews(String diaryViewJson);

    int changeDiaryState(String id, int state);
}
