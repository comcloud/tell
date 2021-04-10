package com.yundingxi.tell.service;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.util.Result;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
public interface SpittingGroovesService{


    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    Result<String> insert(SpittingGrooves entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    Result<String> deleteById(Serializable id);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    Result<String> updateById(Serializable entity);

    /**
     * 吐槽 Vo 信息
     * @return list
     */
    Result<PageInfo<SpittingGroovesVo>> selectAllVo(Integer pageNum);

    /**
     * 根据ID 查询吐槽详细内容
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    Result<SpittingGrooves> selectDetailsById( String id);

    /**
     * 根据吐槽ID 获取 发布吐槽用户ID
     * @param id
     * @return
     */

    String getOpenIdBySID(@Param("id") String id);

}
