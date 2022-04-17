package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-17:15
 */
@Mapper
public interface CommentsMapper {
    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(@Param("entity") Comments entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteById(Serializable id);

    /**
     * 根据ID 查询吐槽详细内容
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    List<CommentVo> selectAll(@Param("id") String id);


}
