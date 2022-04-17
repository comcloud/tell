package com.yundingxi.web.controller;

import com.yundingxi.common.util.Result;
import com.yundingxi.model.vo.AchieveVo;
import com.yundingxi.model.vo.StampVo;
import com.yundingxi.biz.service.AchieveService;
import com.yundingxi.biz.service.StampService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version v1.0
 * @ClassName StampAchieveController
 * @Author rayss
 * @Datetime 2021/5/14 3:28 下午
 */

@RestController
@Api(value = "/stamp", tags = "邮票成就接口")
public class StampAchieveController {

    private final StampService stampService;
    private final AchieveService articleService;

    @Autowired
    public StampAchieveController(StampService stampService, AchieveService articleService) {
        this.stampService = stampService;
        this.articleService = articleService;
    }

    @GetMapping("/getAllStamp")
    @Operation(description = "获取个人邮票", summary = "获取个人邮票")
    public Result<List<StampVo>> getAllStamp(String openId) {
        return stampService.getAllStamp(openId);
    }

    @GetMapping("/getAllAchieve")
    @Operation(description = "获取个人成就", summary = "获取个人成就")
    public Result<List<AchieveVo>> getAllAchieve(String openId) {
        return articleService.getAllAchieve(openId);
    }

    @GetMapping("/getAllStampForAlbum")
    @Operation(description = "获取所有邮票，为了集邮册内容", summary = "获取所有邮票")
    public Result<List<StampVo>> getAllStampForAlbum(@Parameter(description = "open id") String openId) {
        return stampService.getAllStampForAlbum(openId);
    }
}
