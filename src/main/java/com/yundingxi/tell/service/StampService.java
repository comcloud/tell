package com.yundingxi.tell.service;


import com.yundingxi.tell.util.Result;

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
}
