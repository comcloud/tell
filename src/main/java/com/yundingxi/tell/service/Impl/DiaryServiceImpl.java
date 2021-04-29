package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.DiaryViewDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.mapper.DiaryMapper;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.util.JsonUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    private final Logger log = LoggerFactory.getLogger(DiaryServiceImpl.class);

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
            String orderBy = "date desc";
            PageHelper.startPage(pageNum, 10, orderBy);
            List<Diarys> diarys = diaryMapper.selectAllPublicDiary();
            diarys.forEach(diary -> {
                diary.setContent(diary.getContent().length() > 25 ? diary.getContent().substring(0, 25) : diary.getContent());
            });
            return new PageInfo<>(diarys);
        }).get();
    }

    @Override
    @Deprecated
    public void setViews(DiaryViewDto[] viewDtoList) {
        CompletableFuture.runAsync(() -> {
            for (DiaryViewDto diaryViewDto : viewDtoList) {
                diaryMapper.updateDiaryNumber(diaryViewDto.getDiaryId(), diaryViewDto.getViewNum());
            }
        });
    }

    @Override
    public void setViews(String diaryViewJson) {
        CompletableFuture.runAsync(() -> {
//            char[] chars = diaryViewJson.toCharArray();
//            char[] newChar = new char[chars.length + 100];
//            for (int i = 0, j = 0; i < chars.length; i++,j++) {
//                if (chars[i] == '{') {
//                    newChar[j] = '{';
//                    j++;
//                    newChar[j] = '\"';
//                }else if(chars[i] == ',' && chars[i - 1] != '}'){
//                    newChar[j] = ',';
//                    j++;
//                    newChar[j] = '\"';
//                }else if(chars[i] == ':'){
//                    newChar[j] = '\"';
//                    j++;
//                    newChar[j] = ':';
//                }else{
//                    newChar[j] = chars[i];
//                }
//            }
            List<DiaryViewDto> diaryViewDtoList = JsonUtil.parseArray(diaryViewJson, DiaryViewDto[].class);
            log.info(String.valueOf(diaryViewDtoList));
            diaryViewDtoList.forEach(diaryViewDto -> diaryMapper.updateDiaryNumber(diaryViewDto.getDiaryId().replace("\"",""),diaryViewDto.getViewNum()));
        });
    }
}
