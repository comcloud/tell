package com.yundingxi.tell.controller;

import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.*;
import com.yundingxi.tell.bean.vo.IndexLetterVo;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName LetterController
 * @Author rayss
 * @Datetime 2021/3/24 6:14 下午
 */

@RestController
@RequestMapping("/letter")
@Api(value = "/letter", tags = "信件接口")
public class LetterController {

    @Autowired
    private LetterService letterService;

    private final Logger log = LoggerFactory.getLogger(LetterController.class);

    /**
     * 普通发送
     *
     * @param letterStorageDto 发送的信件
     * @return 返回结果
     */

    @PostMapping(value = "/send")
    @Operation(description = "保存信件", summary = "保存信件")
    public Result<String> saveLetter(@Parameter(description = "信件对象", required = true) LetterStorageDto letterStorageDto) {
        return ResultGenerator.genSuccessResult(letterService.saveSingleLetter(letterStorageDto));
    }

    @PostMapping(value = "/reply")
    @Operation(description = "给对方回复信件", summary = "回复信件")
    public Result<String> replyLetter(@Parameter(description = "回复信件的对象", required = true)
                                              LetterReplyDto letterReplyDto) {
        String result = letterService.replyLetter(letterReplyDto);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 获取三封信件
     *
     * @param openId 用户 open id
     * @return 信件结果集
     */
    @GetMapping(value = "/getLetter")
    @Cacheable("letters")
    @Operation(description = "获取信件的用户的open id", summary = "获取三封信件")
    public Result<List<IndexLetterDto>> getLetters(@Parameter(description = "open id", required = true) @RequestParam String openId) {
        return ResultGenerator.genSuccessResult(letterService.getLettersByOpenId(openId));
    }

    @Operation(description = "获取未读回信的数量,这里设定1为回信，2为评论", summary = "获取未读信件数量与评论数量")
    @GetMapping("/getNumberOfLetter")
    public Result<Map<Integer, Integer>> getNumberOfLetter(@Parameter(description = "open id", required = true)
                                                           @RequestParam("openId") String openId) {
        return ResultGenerator.genSuccessResult(letterService.getNumberOfLetter(openId));
    }

    @Operation(description = "获取所有未读信件,分页获取", summary = "获取未读信件")
    @GetMapping("/getAllUnreadLetter")
    public Result<List<UnreadMessageDto>> getAllUnreadLetter(@Parameter(description = "open id", required = true) String openId
            , @Parameter(description = "表示从多少页开始") @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum) {

        return ResultGenerator.genSuccessResult(letterService.getAllUnreadLetter(openId, pageNum));
    }

    @Operation(description = "根据信件id获取信件具体回复信息", summary = "获取回复信件信息")
    @GetMapping("/getDetailOfLetter")
    public Result<LetterDto> getDetailOfLetter(@Parameter(description = "获取信件信息对象，如果只是获取普通获取信息不是回复，那么letterId应该为null") ReplyInfoDto replyInfoDto) {
        return ResultGenerator.genSuccessResult(letterService.getLetterById(replyInfoDto));
    }

    @Operation(description = "根据信件id获取信件的信息", summary = "获取信件信息")
    @GetMapping("/getDetailOfLetterById")
    public Result<IndexLetterDto> getIndexLetterById(@Parameter(description = "信件id") String letterId,
                                                     @Parameter(description = "open id") String openId) {
        return ResultGenerator.genSuccessResult(letterService.getLetterById(new IndexLetterVo(openId, letterId)));
    }

    @GetMapping("/getLetterOfHistory")
    @Operation(description = "获取历史别人给我回复的信件", summary = "获取历史回信")
    public Result<PageInfo<UnreadMessageDto>> getLetterOfHistory(@Parameter(description = "open id", required = true) String openId
            , @Parameter(description = "表示从多少页开始") @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum) {
        return letterService.getLetterOfHistory(openId, pageNum);
    }

    @PutMapping("/changeLetterStateToDeleteById")
    @Operation(description = "更改信件状态为删除状态，起到删除的作用", summary = "删除信件")
    public Result<String> changeLetterStateToDeleteById(@Parameter(description = "要更改的日记id") @RequestParam String id) {
        return letterService.changeLetterState(id, 1) == 1 ? ResultGenerator.genSuccessResult("更改成功") : ResultGenerator.genFailResult("更改失败");
    }
}
