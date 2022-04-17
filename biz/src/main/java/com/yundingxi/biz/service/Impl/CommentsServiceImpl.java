package com.yundingxi.biz.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.common.model.enums.WeChatEnum;
import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.common.util.Result;
import com.yundingxi.common.util.ResultGenerator;
import com.yundingxi.common.util.strategy.SubMessageStrategyContext;
import com.yundingxi.dao.mapper.CommentsMapper;
import com.yundingxi.dao.mapper.SpittingGroovesMapper;
import com.yundingxi.dao.mapper.UserMapper;
import com.yundingxi.dao.model.Comments;
import com.yundingxi.dao.model.SpittingGrooves;
import com.yundingxi.model.vo.CommentVo;
import com.yundingxi.model.vo.UserCommentVo;
import com.yundingxi.model.vo.UserVo;
import com.yundingxi.model.vo.submessage.SubMessageParam;
import com.yundingxi.biz.service.CommentsService;
import com.yundingxi.biz.service.SpittingGroovesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Resource
    private ThreadPoolExecutor businessPool;

    @Override
    public Result<String> insert(Comments entity) {
        int state = commentsMapper.insert(entity);
        if (state > 0) {
            businessPool.execute(() -> {
                SpittingGrooves spittingGrooves = spittingGroovesMapper.selectOpenIdAndContentById(entity.getSgId());
                SubMessageParam param = SubMessageParam.builder()
                        .parentId(entity.getSgId())
                        .showContent(entity.getContent())
                        .title(spittingGrooves.getContent())
                        .touser(spittingGrooves.getOpenId())
                        .sender(entity.getOpenId())
                        .nickname(userMapper.selectPenNameByOpenId(entity.getOpenId()))
                        .templateId(WeChatEnum.SUB_MESSAGE_COMMENT_TEMPLATE_ID)
                        .version(WeChatEnum.SUB_MESSAGE_MINI_PROGRAM_STATE_FORMAL_VERSION).page(WeChatEnum.SUB_MESSAGE_COMMENT_PAGE).build();
                SubMessageStrategyContext.getSubMessageStrategy(WeChatEnum.SUB_MESSAGE_COMMENT_TEMPLATE_ID).processSubMessage(param);
            });
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
