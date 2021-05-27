package com.yundingxi.tell.controller;

import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.QuestionnaireDto;
import com.yundingxi.tell.bean.entity.Questionnaire;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.*;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.ModelUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @Operation(description = "用户注册", summary = "用户注册")
    @PostMapping("/registeredUser")
    public Result<String> registeredUser(@Parameter(description = "用户", required = true) User user) {
        return userService.insertUser(user);
    }

    @GetMapping("/getKey")
    @Operation(description = "根据动态的js code生成用户的open id", summary = "获取open id")
    public Result<String> getKey(@Parameter(description = "js code", required = true)
                                 @RequestParam String jsCode) {
        String openId = userService.getKey(jsCode);
        return ResultGenerator.genSuccessResult(openId);
    }

    @GetMapping("/getAllUserCommentVo")
    @Operation(description = "根据 openid 获取评论信息 ", summary = "根据 openid 获取评论信息")
    public Result<Object> getAllUserCommentVo(@Parameter(description = "openid") String openId, Integer pageNum) {
        return userService.getAllUserCommentVo(openId, pageNum);
    }
//    @GetMapping("/getCommNum")
//    @Operation(description = "获取收到评论未读消息个数 ",summary = "获取收到评论未读消息个数")
//    public Result<Object> getCommNum(@Parameter(description = "openid") String openId){
//        return userService.getCommNum(openId);
//    }

    /**
     * @param user
     * @return
     */
    @PostMapping("/updateUser")
    @Operation(description = "修改用户信息 ", summary = "修改用户信息")
    public Result<String> updateUser(@Parameter(description = "user 对象", required = true) User user) {
        System.out.println(user);
        return userService.updateUser(user);
    }

    /**
     * 用户退出时更新用户最初
     *
     * @param openId 用户OPenID
     * @return
     */
    @PostMapping("/updateOutDate")
    @Operation(description = "用户退出登录 ", summary = "用户退出,更新最后登陆时间")
    public Result<String> updateOutDate(@Parameter(description = "openid", required = true) String openId) {
        return userService.updateOutDate(openId);
    }

    @GetMapping("/getProfile")
    @Operation(description = "获取历史数量信息，包括：解忧、日记、吐槽数量，同时也包含用户昵称与用户头像", summary = "获取个人信息")
    public Result<ProfileVo> getProfile(@Parameter(description = "open id", required = true) String openId) {
        return userService.getProfile(openId);
    }

    @GetMapping("/getDataAnalysis")
    @Operation(description = "获取个人数据分析内容", summary = "获取个人数据分析内容")
    public Result<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> getDataAnalysis(@Parameter(description = "open id", required = true) String openId
            , @Parameter(description = "给定的需要计算的时间的时间戳", required = true) Long currentTimeStamp) {
        return userService.getDataAnalysis(openId, currentTimeStamp);
    }

    @GetMapping("/isTextLegal")
    @Operation(description = "判断文字内容是否属于违规语言,结果类型，1.合规，2.不合规，3.疑似，4.审核失败", summary = "文字违规判断")
    public Result<Integer> textLegal(@Parameter(description = "文字内容") @RequestParam(value = "textContent") String textContent) {
        return userService.isTextLegal(textContent);
    }

    @GetMapping("/getDataOfHistory")
    @Operation(description = "获取所有历史发布内容，包括信件，日记，吐槽", summary = "获取历史发布内容")
    public Result<HistoryDataVo> getDataOfHistory(@Parameter(description = "openid") @RequestParam("openid") String openId) {
        return userService.getDataOfHistory(openId);
    }

    @GetMapping("/getOfficialMsg")
    @Operation(description = "获取官方推送", summary = "获取官方审核推送")
    public Result getOfficialMsg(@Parameter(description = "openid") @RequestParam("openid") String openId) {
        return userService.getOfficialMsg(openId);
    }

    @Operation(description = "获取事件线数据，目前时间线有，我发布了解忧、日记、吐槽其中某一个", summary = "获取时间线数据")
    @GetMapping("/getTimelineData")
    public Result<PageInfo<TimelineVo>> getTimelineData(@Parameter(description = "open id", required = true) String openId
            , @Parameter(description = "页数，默认是1") @RequestParam(defaultValue = "1") Integer pageNum) {
        return userService.getTimelineData(openId, pageNum);
    }

    @Operation(description = "保存调查问卷", summary = "保存调查问卷")
    @PostMapping("/saveQuestionnaire")
    public Result<String> saveQuestionnaire(@Parameter(description = "调查问卷实体类，时间可以不传递") QuestionnaireDto questionnaireDto) {
        return userService.saveQuestionnaire(questionnaireDto);
    }
}
