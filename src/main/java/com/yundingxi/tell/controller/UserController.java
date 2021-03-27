package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-16:13
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @ResponseBody
    @PostMapping("/registeredUser")
    public Result<String> registeredUser(@RequestBody User user){
       return userService.insertUser(user);
    }
}
