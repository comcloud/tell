package com.yundingxi.tell.controller;

import com.yundingxi.tell.bean.entity.Stamp;
import com.yundingxi.tell.bean.vo.StampVo;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.Result;
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

    @Autowired
    private StampService stampService;
    @Autowired
    private AchieveService articleService;

    @GetMapping("/getAllStamp")
    @Operation(description = "获取个人邮票", summary = "获取个人邮票")
    public Result getAllStamp(String openId, Integer pageNum) {
        return stampService.getAllStamp(openId, pageNum);
    }

    @GetMapping("/getAllAchieve")
    @Operation(description = "获取个人成就", summary = "获取个人成就")
    public Result getAllAchieve(String openId, Integer pageNum) {
        return articleService.getAllAchieve(openId, pageNum);
    }

    @GetMapping("/getAllStampForAlbum")
    @Operation(description = "获取所有邮票，为了集邮册内容", summary = "获取所有邮票")
    public Result<List<StampVo>> getAllStampForAlbum(@Parameter(description = "open id") String openId) {
        return stampService.getAllStampForAlbum(openId);
    }
}
