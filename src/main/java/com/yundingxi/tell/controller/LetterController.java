package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version v1.0
 * @ClassName LetterController
 * @Author rayss
 * @Datetime 2021/3/24 6:14 下午
 */

@RestController
@RequestMapping("/letter")
public class LetterController {

    @Autowired
    private LetterService letterService;

    /**
     * 普通发送
     * @param letter 发送的信件
     * @return 返回结果
     */
    @PostMapping(value = "/send")
    public Result<String> saveLetter(@RequestParam Letter letter){
        return ResultGenerator.genSuccessResult(letterService.saveSingleLetter(letter));
    }

    /**
     * 拉去用户未读消息
     * @param openId 用户open id
     * @return 用户未读消息
     */
    @GetMapping(value = "/putUnreadMessage")
    public Result<String> putUnreadMessage(@RequestParam("openId") String openId){
        String letterJson = letterService.putUnreadMessage(openId);
        return ResultGenerator.genSuccessResult(letterJson);
    }

    /**
     * 获取三封信件
     * @param openId 用户 open id
     * @return 信件结果集
     */
    @GetMapping(value = "/getLetter")
    public Result<List<Letter>> getLetters(String openId){
        return ResultGenerator.genSuccessResult(letterService.getLettersByOpenId(openId));
    }
}
