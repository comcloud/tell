package com.yundingxi.biz.service.Impl;


import com.yundingxi.biz.service.AchieveService;
import com.yundingxi.common.util.redis.RedisUtil;
import com.yundingxi.common.util.response.Result;
import com.yundingxi.common.util.response.ResultGenerator;
import com.yundingxi.dao.mapper.AchieveMapper;
import com.yundingxi.dao.model.Achieve;
import com.yundingxi.model.vo.AchieveVo;
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

    @Override
    public List<String> selectAllAchieveType() {
        return achieveMapper.selectAllAchieveType();
    }
}
