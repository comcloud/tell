package com.yundingxi.tell.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.common.enums.RedisEnums;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.service.CommentsService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 吐槽实现类
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
@Slf4j
@Service
public class SpittingGroovesServiceImpl implements SpittingGroovesService {

    @Autowired
    private SpittingGroovesMapper spittingGroovesMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommentsService commentsService;

    @Override
    public Result<String> insert(SpittingGrooves entity) {
        int state = spittingGroovesMapper.insert(entity);
        if (state > 0) {
            log.info("===================> {} 数据保存成功", entity);
            return ResultGenerator.genSuccessResult("发布成功");
        } else {
            log.info("===================> {} 数据保存失败", entity);
            return ResultGenerator.genFailResult("发布失败");
        }
    }

    @Override
    public Result<String> deleteById(Serializable id) {
        int state = spittingGroovesMapper.deleteById(id);
        if (state > 0) {
            log.info("=====================> id 为 {} 的数据删除成功", id);
            return ResultGenerator.genSuccessResult("删除成功");
        } else {
            log.info("=====================> id 为 {} 的数据删除失败", id);
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    @Override
    public Result<String> updateById(Serializable entity) {
        int state = spittingGroovesMapper.updateById(entity);
        if (state > 0) {
            log.info("=====================>  为 {} 的数据更新成功", entity);
            return ResultGenerator.genSuccessResult("更改成功");
        } else {
            log.info("=====================>  为 {} 的数据更新失败", entity);
            return ResultGenerator.genFailResult("更改失败");
        }
    }

    @Override
    public Result<PageInfo<SpittingGroovesVo>> selectAllVo(Integer pageNum) {
        String orderBy = "sg.date desc";
        PageHelper.startPage(pageNum, 10, orderBy);
        List<SpittingGroovesVo> spittingGroovesVos = spittingGroovesMapper.selectAllVo();
        spittingGroovesVos.forEach(this::subStringTitle);
        PageInfo<SpittingGroovesVo> pageInfo = new PageInfo<>(spittingGroovesVos);
        log.info("=====================> 查询数据成功 {}", "");
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Override
    public void subStringTitle(SpittingGroovesVo spittingGroovesVo) {
        String title = spittingGroovesVo.getTitle();
        int i = title.indexOf("。", 20);

        if (i == -1) {
            if (title.length() > 31) {
                spittingGroovesVo.setTitle(title.substring(0, 25));
            }
        } else {
            if (i > 20 && i <= 30) {
                spittingGroovesVo.setTitle(title.substring(0, i));
            } else if (i > 30) {
                spittingGroovesVo.setTitle(title.substring(0, 25));
            }
        }

    }

    @Override
    public Result<SpittingGrooves> selectDetailsById(String id) {
        redisUtil.select(RedisEnums.USER_SPITTINGGROOVES_JSON.getRedisDbIndex());

        SpittingGrooves spittingGroove = spittingGroovesMapper.selectDetailsById(id);
        return ResultGenerator.genSuccessResult(spittingGroove);

    }

    @Override
    public String getOpenIdBySID(String id) {
        return spittingGroovesMapper.getOpenIdBySID(id);
    }
}
