package com.yundingxi.tell.service.Impl;


import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.vo.AchieveVo;
import com.yundingxi.tell.mapper.AchieveMapper;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class AchieveServiceImpl implements AchieveService {
    @Autowired
    private AchieveMapper achieveMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result<List<AchieveVo>> getAllAchieve(String openId) {
        List<AchieveVo> achieveVos = new ArrayList<>();
        String achieveUnreadNumKey = "listener:" + openId + ":achieve_unread_num";
        redisUtil.set(achieveUnreadNumKey, 0);
        List<Achieve> allAchieveList = achieveMapper.selectAllAchieve();
        allAchieveList.forEach(achieve -> achieveVos.add(new AchieveVo(achieve.getAchieveUrl(), achieve.getAchieveDesc(), achieve.getAchieveEdition(), achieve.getAchieveName(), null, true)));
        List<AchieveVo> haveAchieveList = achieveMapper.haveListMeAll(openId);
        achieveVos.forEach(achieveVo -> {
            for (AchieveVo haveAchieve : haveAchieveList) {
                if (haveAchieve.getAchieveName().equals(achieveVo.getAchieveName())) {
                    achieveVo.setLock(false);
                    achieveVo.setObtainTime(haveAchieve.getObtainTime());
                    break;
                }
            }
        });
        return ResultGenerator.genSuccessResult(achieveVos);
    }
}
