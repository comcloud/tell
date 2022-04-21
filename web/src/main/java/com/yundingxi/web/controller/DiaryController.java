package com.yundingxi.web.controller;

import com.github.pagehelper.PageInfo;
import com.yundingxi.biz.infrastructure.mq.KafkaProducer;
import com.yundingxi.biz.model.KafkaMessage;
import com.yundingxi.common.model.constant.CommonConstant;
import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.response.Result;
import com.yundingxi.common.util.response.ResultGenerator;
import com.yundingxi.dao.model.Diarys;
import com.yundingxi.model.dto.Diary.DiaryDto;
import com.yundingxi.biz.service.DiaryService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * @version v1.0
 * @ClassName DiaryController
 * @Author rayss
 * @Datetime 2021/3/30 10:22 上午
 */

@RestController
@RequestMapping(value = "/diary")
@Api(value = "/diary", tags = "日记接口")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Resource
    private KafkaProducer<KafkaMessage<?>> kafkaProducer;

    @Operation(description = "保存用户日记", summary = "保存日记")
    @PostMapping("/saveDiary")
    public Result<Object> saveDiary(@Parameter(description = "日记对象")
                                            DiaryDto diaryDto) {
        if (diaryService.saveDiary(diaryDto) == 1) {
//            publisher.publishEvent(new UserBehaviorEvent<>(this, diaryDto));
            kafkaProducer.sendMessage(CommonConstant.ACHIEVE_STAMP_TOPIC, AchieveStampEnum.DIARY_TYPE, KafkaMessage.builder().object(diaryDto).build());
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(new Object());
        }

    }

    @Operation(description = "更改日记状态为删除状态，起到删除的作用", summary = "删除日记")
    @PostMapping("/changeDiaryStateToDeleteById")
    public Result<String> changeDiaryStateToDeleteById(@Parameter(description = "要更改的日记id") @RequestParam String id) {
        return diaryService.changeDiaryState(id, 4) == 1 ? ResultGenerator.genSuccessResult("更改成功") : ResultGenerator.genFailResult("更改失败");
    }

    /**
     * 返回一个用户发布的所有日记，这个信件不包含日记的详细内容
     *
     * @param openId 单个用户的open id
     * @return 此用户的发布的所有日记
     */
    @Operation(description = "获取一个用户的历史所有日记,但是不包含每篇日记的详细内容", summary = "获取历史发布日记")
    @GetMapping("/getAllDiaryForSelf")
    @Cacheable("all_diary")
    public Result<List<Diarys>> getAllDiaryForSelf(@Parameter(description = "open id")
                                                           String openId) {
        return ResultGenerator.genSuccessResult(diaryService.getAllDiaryForSelfByOpenId(openId));
    }

    @Operation(description = "获取广场发布日记", summary = "获取广场日记")
    @GetMapping("/getAllDiary")
    public Result<PageInfo<Diarys>> getAllDiary(@Parameter(description = "表示从多少页开始") @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum) {
        return ResultGenerator.genSuccessResult(diaryService.getAllPublicDiary(pageNum));
    }

    @Operation(description = "根据日记的id获取这篇日记的详细内容", summary = "获取日记内容")
    @GetMapping("/getDetailForDiary")
    @Cacheable("diary_detail")
    public Result<Diarys> getDetailForDiary(@Parameter(description = "日记id")
                                                    String id) {
        return ResultGenerator.genSuccessResult(diaryService.getDetailById(id));
    }

    @Operation(description = "更新日记浏览量,传入参数为一个数组", summary = "更新日记浏览量")
    @PostMapping("/setViews")
    public Result<Object> setViews(@Parameter(description = "日记浏览量键值对json串,日记id作为键，浏览量作为值")
                                   @RequestParam("diaryViewJson") String diaryViewJson) {
        diaryService.setViews(diaryViewJson);
        return ResultGenerator.genSuccessResult();
    }
}
