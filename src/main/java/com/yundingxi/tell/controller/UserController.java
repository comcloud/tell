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

    @PostMapping("/registeredUser")
    public Result<String> registeredUser(@RequestBody User user) {
        return userService.insertUser(user);
    }

    @GetMapping("/getKey")
    @Operation(description = "根据动态的js code生成用户的open id")
    public Result<String> getKey(@Parameter(description = "js code", required = true)
                                     @RequestParam String jsCode) {
        String openId = userService.getKey(jsCode);
        return ResultGenerator.genSuccessResult(openId);
    }

}
