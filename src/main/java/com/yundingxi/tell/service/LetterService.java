package com.yundingxi.tell.service;

import com.yundingxi.tell.bean.dto.LetterReplyDto;
import com.yundingxi.tell.bean.dto.UnreadMessageDto;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.Reply;

import java.util.List;

/**
 * @version v1.0
 * @ClassName LetterService
 * @Author rayss
 * @Datetime 2021/3/24 6:30 下午
 */

public interface LetterService {
    /**
     * 保存单封信件到数据库，返回信件的送达时间
     * @param letter 信件对象
     * @return 返回信件的送达时间
     */
    String saveSingleLetter(Letter letter);

    /**
     * 拉取用户唯独的消息
     * @param openId 用户的open id
     * @return 唯独消息的Json串
     */
    UnreadMessageDto putUnreadMessage(String openId);

    /**
     * 获取信件要保证以下结果
     * 只要不是当天登陆，都会进行重新获取三封信
     * @param openId 用户 open id
     * @return 结果集
     */
    List<Letter> getLettersByOpenId(String openId);

    void saveReplyFromSenderToRecipient(Reply reply);

    /**
     * @param letterReplyDto 回复信件的类
     * @return 信件何时会被送达
     */
    String replyLetter(LetterReplyDto letterReplyDto);
}
