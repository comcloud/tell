package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.DiaryViewDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.mapper.DiaryMapper;
import com.yundingxi.tell.service.DiaryService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        CompletableFuture.runAsync(() -> {
            Diarys diarys = BeanUtil.toBean(diaryDto, Diarys.class);
            diarys.setId(UUID.randomUUID().toString());
            diarys.setDate(new Date());
            diarys.setNumber("0");
            diaryMapper.insertSingleDiary(diarys);
        });
    }

    @Override
    public void removeDiaryById(String id) {
        CompletableFuture.runAsync(() -> diaryMapper.deleteSingleDiaryById(id));
    }

    @SneakyThrows
    @Override
    public List<Diarys> getAllDiaryForSelfByOpenId(String openId) {
        return CompletableFuture.supplyAsync(() -> diaryMapper.selectAllDiaryByOpenId(openId)).get();
    }

    @SneakyThrows
    @Override
    public Diarys getDetailById(String id) {
        return CompletableFuture.supplyAsync(() -> diaryMapper.selectSingleDiary(id)).get();
    }

    @SneakyThrows
    @Override
    public List<Diarys> getAllPublicDiary() {
        return CompletableFuture.supplyAsync(() -> diaryMapper.selectAllPublicDiary()).get();
    }

    @SneakyThrows
    @Override
    public PageInfo<Diarys> getAllPublicDiary(Integer pageNum) {
        return CompletableFuture.supplyAsync(() -> {
            String orderBy = "id desc";
            PageHelper.startPage(pageNum, 10, orderBy);
            List<Diarys> diarys = diaryMapper.selectAllPublicDiary();
            return new PageInfo<>(diarys);
        }).get();
    }

    @Override
    public void setViews(DiaryViewDto[] viewDtoList) {
        CompletableFuture.runAsync(() -> {
            for (DiaryViewDto diaryViewDto : viewDtoList) {
                diaryMapper.updateDiaryNumber(diaryViewDto.getDiaryId(), diaryViewDto.getViewNum());
            }
        });
    }
}
