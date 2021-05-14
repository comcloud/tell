package com.yundingxi.tell.service;


import com.yundingxi.tell.bean.entity.Stamp;
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
    Result getAllStamp(String openId, Integer pageNum);

    Result<List<Stamp>> getAllStampForAlbum();
}
