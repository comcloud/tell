package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.dto.UnreadMessageDto;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 普通发送
     *
     * @param letter 发送的信件
     * @return 返回结果
     */

    @PostMapping(value = "/send")
    @Operation(description = "保存信件")
    public Result<String> saveLetter(@Parameter(description = "信件对象",required = true)
                                          Letter letter) {
        return ResultGenerator.genSuccessResult(letterService.saveSingleLetter(letter));
    }

    /**
     * 拉去用户未读消息
     *
     * @param openId 用户open id
     * @return 用户未读消息
     */
    @GetMapping(value = "/putUnreadMessage")
    @Operation(description = "根据用户的open id获取此用户的未读消息")
    public Result<UnreadMessageDto> putUnreadMessage(@Parameter(description = "open id", required = true)
                                               @RequestParam("openId") String openId) {
        UnreadMessageDto messageDto = letterService.putUnreadMessage(openId);
        return ResultGenerator.genSuccessResult(messageDto);
    }

    /**
     * 获取三封信件
     *
     * @param openId 用户 open id
     * @return 信件结果集
     */
    @GetMapping(value = "/getLetter")
    @Operation(description = "获取信件的用户的open id")
    public Result<List<Letter>> getLetters(@Parameter(description = "open id", required = true) String openId) {
        return ResultGenerator.genSuccessResult(letterService.getLettersByOpenId(openId));
    }
}
