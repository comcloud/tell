package com.yundingxi.biz.service.Impl;

import com.yundingxi.biz.service.StampService;
import com.yundingxi.common.util.redis.RedisUtil;
import com.yundingxi.common.util.response.Result;
import com.yundingxi.common.util.response.ResultGenerator;
import com.yundingxi.dao.mapper.StampMapper;
import com.yundingxi.dao.model.Stamp;
import com.yundingxi.dao.model.UserStamp;
import com.yundingxi.model.vo.StampVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class StampServiceImpl implements StampService {
    @Autowired
    private StampMapper stampMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public Result<List<StampVo>> getAllStamp(String openId) {
        //将redis缓存中存储未读邮票个数清为0
        String stampUnreadNumKey = "listener:" + openId + ":stamp_unread_num";
        redisUtil.set(stampUnreadNumKey,0);
        List<StampVo> stampVoList = stampMapper.haveListMeAll(openId);
        return ResultGenerator.genSuccessResult(stampVoList);
    }

    @Override
    public Result<List<StampVo>> getAllStampForAlbum(String openId) {
        List<StampVo> stampVos = new ArrayList<>();
        List<Stamp> stampList = stampMapper.selectAllStamp();
        stampList.forEach(stamp -> stampVos.add(new StampVo(stamp.getStampUrl(),stamp.getStampName(),stamp.getStampSeries(),stamp.getStampNumber(),stamp.getStampDesc(),stamp.getStampEdition(),null,true)));
        List<StampVo> haveStampVoList = stampMapper.haveListMeAll(openId);
        stampVos.forEach(stampVo -> {
            for (StampVo vo : haveStampVoList) {
                if(vo.getStampNumber().equals(stampVo.getStampNumber())){
                    stampVo.setLock(false);
                    break;
                }
            }
        });
        return ResultGenerator.genSuccessResult(stampVos);
    }

    @Override
    public void insertDefaultStamp(List<UserStamp> userStampList) {
        userStampList.forEach(userStamp -> stampMapper.insertSingleNewUserStamp(userStamp));
    }
}
