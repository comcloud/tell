package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
@Mapper
public interface SpittingGroovesMapper {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(@Param("entity") SpittingGrooves entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteById(Serializable id);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    int updateById(@Param("entity") Serializable entity);

    /**
     * 吐槽 Vo 信息
     * @return list
     */
    List<SpittingGroovesVo> selectAllVo();

    /**
     * 根据ID 查询吐槽详细内容
     * @param id 吐槽ID
     * @return 吐槽详细信息
     */
    SpittingGrooves selectDetailsById(@Param("id") String id);

    List<String> getAllID();

    String getOpenIdBySID(@Param("id") String id);

    String getConById(@Param("id")String id);

    int addNumber(@Param("id")String id);

    List<String> selectAllSpitContentByOpenId(@Param("openId") String openId,@Param("currentTime") String currentTime);
}
