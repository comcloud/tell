package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.entity.Comments;
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
    public Result<String> registeredUser(@Parameter(description = "用户",required = true) User user) {
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

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/updateUser")
    @Operation(description = "修改用户信息 ",summary = "修改用户信息")
    public Result<String> updateUser(@Parameter(description = "user 对象",required = true) User user){
        System.out.println(user);
        return userService.updateUser(user);
    }

    /**
     * 用户退出时更新用户最初
     * @param openId  用户OPenID
     * @return
     */
    @PostMapping("/updateOutDate")
    @Operation(description = "用户退出登录 ",summary = "用户退出,更新最后登陆时间")
    public Result<String> updateOutDate(@Parameter(description = "openid",required = true) String openId){
        return userService.updateOutDate(openId);
    }

}
