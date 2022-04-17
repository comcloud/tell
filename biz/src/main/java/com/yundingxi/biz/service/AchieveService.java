package com.yundingxi.web.biz.service;


import com.yundingxi.tell.bean.vo.AchieveVo;
import com.yundingxi.tell.util.Result;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
public interface AchieveService {
    Result<List<AchieveVo>> getAllAchieve(String openId);

}
