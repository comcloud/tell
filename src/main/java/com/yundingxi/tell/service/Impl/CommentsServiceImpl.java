package com.yundingxi.tell.service.Impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Comments;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.*;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.CommentsService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        if (state > 0) {
            /*
             * 订阅消息
             *  - 获取access_token
             *  - 获取接受者open id,通过被回复吐槽的id来查询
             *  - template_id 固定模版ID
             *  - 拼接页面要跳转的page，需要page?id=id
             *  - 组成JSON串data
             *  - 固定miniProgram_state="trial"，体验版
             * */
            String accessToken = InternetUtil.getAccessToken();
            String id = entity.getSgId();
            SpittingGrooves spittingGrooves = spittingGroovesMapper.selectOpenIdAndTitleById(id);
            SubMessageDataVo data = new SubMessageDataVo(
                    new SubMessageValueVo(entity.getContent())
            ,new SubMessageValueVo(userMapper.selectPenNameByOpenId(entity.getOpenId()))
            ,new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            ,new SubMessageValueVo(spittingGrooves.getTitle().length() >=20 ? spittingGrooves.getTitle().substring(0,20):spittingGrooves.getTitle()));
            GeneralDataProcessUtil.subMessage(new SubMessageVo(
                    accessToken
                    , spittingGrooves.getOpenId()
                    , "mghtoN9x1YBMmyWg9RtBlt8-XxHxMvEo8eAtHIazD34"
                    , "packageWriteLetter/pages/complaintletter/complaintletter?id=" + id
                    , data
                    , "trial"));
            log.info("===================> {} 数据保存成功", entity);
            //未读消息+1
            UserVo userVoo = userMapper.getUserVoById(entity.getOpenId());
            UserCommentVo userCommentVo = new UserCommentVo(entity.getSgId(), entity.getContent(), new Date(), spittingGroovesMapper.getConById(entity.getSgId()), userVoo);
            String idBySgId = userMapper.getIDBySgId(entity.getSgId());
            if (!entity.getOpenId().equals(idBySgId)) {
                redisUtil.incr("comm:" + spittingGroovesService.getOpenIdBySID(entity.getSgId()) + ":count", 1);
                redisUtil.rSet("comm:" + spittingGroovesService.getOpenIdBySID(entity.getSgId()) + ":info", userCommentVo);
            }

            return ResultGenerator.genSuccessResult("发布成功");
        } else {
            log.info("===================> {} 数据保存失败", entity);
            return ResultGenerator.genFailResult("发布失败");
        }
    }

    @Override
    public Result<String> deleteById(Serializable id) {
        int state = commentsMapper.deleteById(id);
        if (state > 0) {
            log.info("=====================> id 为 {} 的数据删除成功", id);
            return ResultGenerator.genSuccessResult("删除成功");
        } else {
            log.info("=====================> id 为 {} 的数据删除失败", id);
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    @Override
    public Result<PageInfo<CommentVo>> selectAll(String id, Integer pageNum) {
        String orderBy = "date desc";
        PageHelper.startPage(pageNum, 5, orderBy);
        List<CommentVo> spittingGrooves = commentsMapper.selectAll(id);
        PageInfo<CommentVo> pageInfo = new PageInfo<>(spittingGrooves);
        log.info("=====================> 查询数据成功 {}", pageInfo);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
