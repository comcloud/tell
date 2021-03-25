package com.yundingxi.tell.service;

import com.yundingxi.tell.bean.entity.Letter;

/**
 * @version v1.0
 * @ClassName LetterService
 * @Author rayss
 * @Datetime 2021/3/24 6:30 下午
 */

public interface LetterService {
    String saveSingleLetter(Letter letter);

    /**
     * 拉取用户唯独的消息
     * @param openId 用户的open id
     * @return 唯独消息的Json串
     */
    String putUnreadMessage(String openId);
}
