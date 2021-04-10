package com.yundingxi.tell.controller;

import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.vo.CommentVo;
import com.yundingxi.tell.service.CommentsService;
import com.yundingxi.tell.util.Result;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-20:57
 */
@RestController
@ResponseBody
@RequestMapping("/comments")
@Api(value = "/comments", tags = "吐槽评论接口")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;
    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    @PostMapping("/insert")
    @Operation(description = "保存/发布 吐槽 评论" ,summary = "保存/发布 吐槽 评论")
    Result<String> insert(@Parameter(description = "吐槽评论类对象",required = true)Comments entity) {
        return commentsService.insert(entity);
    }

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    @PostMapping("delete")
    @Operation(description = "根据吐槽评论ID删除吐槽评论" ,summary = "根据吐槽评论ID删除吐槽评论")
    Result<String> deleteById(Serializable id) {
        return  commentsService.deleteById(id);
    }

    /**
     * 根据ID 查询全部评论
     *
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    @GetMapping("/selectAll")
    @Operation(description = "分页返回吐槽信息",summary = "分页返回吐槽信息")
    Result<PageInfo<CommentVo>> selectAll(@Parameter(description = "吐槽类对象ID",required = true)String id, Integer pageNum) {
        return commentsService.selectAll(id,pageNum);
    }

}
