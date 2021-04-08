package com.yundingxi.tell.service;

import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.CommentVo;
import com.yundingxi.tell.util.Result;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-20:37
 */
public interface CommentsService {
    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    Result<String> insert(com.yundingxi.tell.bean.entity.Comments entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    Result<String> deleteById(Serializable id);

    /**
     * 根据ID 查询全部评论
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    Result<PageInfo<CommentVo>> selectAll(String id, Integer pageNum);
}