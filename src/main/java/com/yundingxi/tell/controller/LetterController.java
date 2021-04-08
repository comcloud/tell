package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.dto.LetterDto;
import com.yundingxi.tell.bean.dto.LetterReplyDto;
import com.yundingxi.tell.bean.dto.UnreadMessageDto;
import com.yundingxi.tell.bean.entity.Letter;
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
import org.springframework.cache.annotation.Caching;
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

    private Logger log = LoggerFactory.getLogger(LetterController.class);
    /**
     * 普通发送
     *
     * @param letter 发送的信件
     * @return 返回结果
     */

    @PostMapping(value = "/send")
    @Operation(description = "保存信件",summary = "保存信件")
    public Result<String> saveLetter(@Parameter(description = "信件对象",required = true) Letter letter) {
        return ResultGenerator.genSuccessResult(letterService.saveSingleLetter(letter));
    }

    @PostMapping(value = "/reply")
    @Operation(description = "给对方回复信件",summary = "回复信件")
    public Result<String> replyLetter(@Parameter(description = "回复信件的对象",required = true)
                                                  LetterReplyDto letterReplyDto){
        String result =  letterService.replyLetter(letterReplyDto);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 拉去用户未读消息
     *
     * @param openId 用户open id
     * @return 用户未读消息
     */
//    @GetMapping(value = "/putUnreadMessage")
//    @Operation(description = "根据用户的open id获取此用户的未读消息",summary = "拉取未读消息")
//    public Result<UnreadMessageDto> putUnreadMessage(@Parameter(description = "open id", required = true)
//                                               @RequestParam("openId") String openId) {
//        UnreadMessageDto messageDto = letterService.putUnreadMessage(openId);
//        return ResultGenerator.genSuccessResult(messageDto);
//    }

    /**
     * 获取三封信件
     *
     * @param openId 用户 open id
     * @return 信件结果集
     */
    @GetMapping(value = "/getLetter")
    @Cacheable("letters")
    @Operation(description = "获取信件的用户的open id",summary = "获取三封信件")
    public Result<List<LetterDto>> getLetters(@Parameter(description = "open id", required = true) @RequestParam String openId) {
        log.info("没有缓存");
        return ResultGenerator.genSuccessResult(letterService.getLettersByOpenId(openId));
    }

    @Operation(description = "获取未读回信的数量,这里设定1为回信，2为评论",summary = "获取未读信件数量")
    @GetMapping("/getNumberOfLetter")
    public Result<Map<Integer,Integer>> getNumberOfLetter(@Parameter(description = "open id", required = true)
                                                          @RequestParam("openId") String openId){
        return ResultGenerator.genSuccessResult(letterService.getNumberOfLetter(openId));
    }

    @Operation(description = "获取所有未读信件，注意这里接口调用之后后台将会将此信件标记未已读，需要前台缓存这个数据",summary = "获取未读信件")
    @GetMapping("/getAllUnreadLetter")
    public Result<List<UnreadMessageDto>> getAllUnreadLetter(@Parameter(description = "open id") String openId){
        return ResultGenerator.genSuccessResult(letterService.getAllUnreadLetter(openId));
    }

    @Operation(description = "根据信件id获取信件具体信息",summary = "获取信件信息")
    @GetMapping("/getDetailOfLetter")
    public Result<LetterDto> getDetailOfLetter(@Parameter(description = "信件id") String letterId){
        return ResultGenerator.genSuccessResult(letterService.getLetterById(letterId));
    }
}
