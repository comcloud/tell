package com.yundingxi.biz.service;



import com.yundingxi.common.util.Result;
import com.yundingxi.dao.model.UserStamp;
import com.yundingxi.model.vo.StampVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
public interface StampService  {
    Result<List<StampVo>> getAllStamp(String openId);

    Result<List<StampVo>> getAllStampForAlbum(String openId);

    void insertDefaultStamp(List<UserStamp> userStampList);
}
