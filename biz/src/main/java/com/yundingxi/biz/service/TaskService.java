package com.yundingxi.biz.service;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
public interface TaskService {

    void stampAndAchieveInitForEveryone(String openId, boolean isForce);
}
