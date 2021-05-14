package com.yundingxi.tell.service.Impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.vo.AchieveVo;
import com.yundingxi.tell.bean.vo.StampVo;
import com.yundingxi.tell.mapper.AchieveMapper;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class AchieveServiceImpl  implements AchieveService {
    @Autowired
    private AchieveMapper achieveMapper;

    @Override
    public Result<List<AchieveVo>> getAllAchieve(String openId) {
//        String orderBy = "ua.obtain_time desc";
//        PageHelper.startPage(pageNum,10,orderBy);
        List<AchieveVo> haveAchieve = achieveMapper.haveListMeAll(openId);
//        PageInfo<AchieveVo> pageInfo = new PageInfo<>(stampVo);
//        Map<String, Object> stringObjectHashMap = new HashMap<>(2);
//        stringObjectHashMap.put("have",pageInfo);
//        stringObjectHashMap.put("notHave",achieveMapper.notHaveListMeAll(openId));
        return ResultGenerator.genSuccessResult(haveAchieve);
    }
}
