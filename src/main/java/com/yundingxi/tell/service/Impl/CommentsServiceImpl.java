package com.yundingxi.tell.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.vo.CommentVo;
import com.yundingxi.tell.bean.vo.UserCommentVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.CommentsService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.JsonUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-20:38
 */
@Service
@Slf4j
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private SpittingGroovesService spittingGroovesService;
    @Autowired
    private SpittingGroovesMapper spittingGroovesMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Override
    public Result<String> insert(Comments entity) {
        int state = commentsMapper.insert(entity);
        if (state>0){
            log.info("===================> {} 数据保存成功" ,entity);
            redisUtil.incr("comm:"+spittingGroovesService.getOpenIdBySID(entity.getSgId())+":count",1);
            UserCommentVo userCommentVo = new UserCommentVo(entity.getSgId(),entity.getContent(),new Date(), spittingGroovesMapper.getConById(entity.getSgId()),userMapper.getUserVoById(entity.getOpenId()));
            redisUtil.lSet("comm:"+spittingGroovesService.getOpenIdBySID(entity.getSgId())+":info", userCommentVo);
            spittingGroovesMapper.addNumber(entity.getSgId());
            return ResultGenerator.genSuccessResult("发布成功");
        }else {
            log.info("===================> {} 数据保存失败" ,entity);
            return ResultGenerator.genFailResult("发布失败");
        }
    }

    @Override
    public Result<String> deleteById(Serializable id) {
        int state = commentsMapper.deleteById(id);
        if (state>0){
            log.info("=====================> id 为 {} 的数据删除成功",id);
            return ResultGenerator.genSuccessResult("删除成功");
        }else {
            log.info("=====================> id 为 {} 的数据删除失败",id);
            return ResultGenerator.genFailResult("删除失败");
        }
    }


    @Override
    public Result<PageInfo<CommentVo>> selectAll(String id,Integer pageNum) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum,5,orderBy);
        List<CommentVo> spittingGrooves = commentsMapper.selectAll(id);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(spittingGrooves);
        log.info("=====================> 查询数据成功 {}",pageInfo);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
