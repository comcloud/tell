package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.DiaryViewDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.mapper.DiaryMapper;
import com.yundingxi.tell.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @version v1.0
 * @ClassName DiaryServiceImpl
 * @Author rayss
 * @Datetime 2021/3/30 10:42 上午
 */

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    @Override
    public void saveDiary(DiaryDto diaryDto) {
        Diarys diarys = BeanUtil.toBean(diaryDto, Diarys.class);
        diarys.setId(UUID.randomUUID().toString());
        diarys.setDate(new Date());
        diarys.setNumber("0");
        diaryMapper.insertSingleDiary(diarys);
    }

    @Override
    public void removeDiaryById(String id) {
        diaryMapper.deleteSingleDiaryById(id);
    }

    @Override
    public List<Diarys> getAllDiaryForSelfByOpenId(String openId) {
        return diaryMapper.selectAllDiaryByOpenId(openId);
    }

    @Override
    public Diarys getDetailById(String id) {
        return diaryMapper.selectSingleDiary(id);
    }

    @Override
    public List<Diarys> getAllPublicDiary() {
        return diaryMapper.selectAllPublicDiary();
    }

    @Override
    public PageInfo<Diarys> getAllPublicDiary(Integer pageNum) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum,10,orderBy);
        List<Diarys> diarys = diaryMapper.selectAllPublicDiary();
        return new PageInfo<>(diarys);

    }

    @Override
    public void setViews(DiaryViewDto[] viewDtoArray) {
        for (DiaryViewDto viewDto : viewDtoArray) {
            diaryMapper.updateDiaryNumber(viewDto.getDiaryId(),viewDto.getViewNum());
        }
    }
}
