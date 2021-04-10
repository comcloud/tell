package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-16:13
 */
@RestController
@RequestMapping("/user")
@Api(value = "/user", tags = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(description = "用户注册",summary = "用户注册")
    @PostMapping("/registeredUser")
    public Result<String> registeredUser(@RequestBody User user) {
        return userService.insertUser(user);
    }

    @GetMapping("/getKey")
    @Operation(description = "根据动态的js code生成用户的open id",summary = "获取open id")
    public Result<String> getKey(@Parameter(description = "js code", required = true)
                                     @RequestParam String jsCode) {
        String openId = userService.getKey(jsCode);
        return ResultGenerator.genSuccessResult(openId);
    }
    @GetMapping("/getAllUserCommentVo")
    @Operation(description = "根据 openid 获取评论信息 ",summary = "根据 openid 获取评论信息")
    public Result<Object> getAllUserCommentVo(@Parameter(description = "openid") String openId) {
        return userService.getAllUserCommentVo(openId);
    }
    @GetMapping("/getCommNum")
    @Operation(description = "获取收到评论未读消息个数 ",summary = "获取收到评论未读消息个数")
    public Result<Object> getCommNum(@Parameter(description = "openid") String openId){
        return userService.getCommNum(openId);
    }

}
