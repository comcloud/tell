package com.yundingxi.biz.service;



import com.yundingxi.common.util.Result;
import com.yundingxi.model.vo.AchieveVo;

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
