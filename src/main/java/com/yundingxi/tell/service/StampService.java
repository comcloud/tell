package com.yundingxi.tell.service;


import com.yundingxi.tell.bean.entity.UserStamp;
import com.yundingxi.tell.bean.vo.StampVo;
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
public interface StampService  {
    Result<List<StampVo>> getAllStamp(String openId);

    Result<List<StampVo>> getAllStampForAlbum(String openId);

    void insertDefaultStamp(List<UserStamp> userStampList);
}
