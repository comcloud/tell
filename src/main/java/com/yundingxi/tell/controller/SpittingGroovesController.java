package com.yundingxi.tell.controller;


import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.Result;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
@RestController
@ResponseBody
@RequestMapping("/spitting-grooves")
@Api(value = "/spitting-grooves" ,tags = "吐槽接口")

public class SpittingGroovesController {
    @Autowired
    private SpittingGroovesService spittingGroovesService;
    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    @PostMapping("/insert")
    @Operation(description = "保存/发布 吐槽")
    Result<String> insert(@Parameter(description = "吐槽类对象",required = true)SpittingGrooves entity){
        return spittingGroovesService.insert(entity);
    }

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    @PostMapping("delete")
    @Operation(description = "根据吐槽ID删除吐槽")
    Result<String> deleteById(@Parameter(description = "吐槽ID" ,required=true) Serializable id){
        return  spittingGroovesService.deleteById(id);
    }

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    @PostMapping("/update")
    Result<String> updateById(@Parameter(description = "吐槽类对象",required = true)SpittingGrooves entity){
        return spittingGroovesService.updateById(entity);
    }

    /**
     * 吐槽 Vo 信息
     * @return list
     */
    @GetMapping("/selectAllVo")
    @Operation(description = "分页返回吐槽信息")
    Result<PageInfo<SpittingGroovesVo>> selectAllVo(@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum){
        return spittingGroovesService.selectAllVo(pageNum);
    }

    /**
     * 根据ID 查询吐槽详细内容
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    @GetMapping("/selectDetailsById")
    Result<SpittingGrooves> selectDetailsById(@Parameter(description = "吐槽类对象ID",required = true) String id){
        return spittingGroovesService.selectDetailsById(id);
    }
}
