package com.yundingxi.biz.service;

import com.github.pagehelper.PageInfo;
import com.yundingxi.common.util.Result;
import com.yundingxi.dao.model.Reply;
import com.yundingxi.model.dto.letter.*;
import com.yundingxi.model.vo.IndexLetterVo;
import com.yundingxi.tell.bean.dto.*;


import java.util.List;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName LetterService
 * @Author rayss
 * @Datetime 2021/3/24 6:30 下午
 */

public interface LetterService {
    /**
     * 保存单封信件到数据库，返回信件的送达时间
     * @param letterStorageDto 信件对象
     * @return 返回信件的保存情况
     */
    Integer saveSingleLetter(LetterStorageDto letterStorageDto);

    /**
     * 拉取用户唯独的消息
     * @param openId 用户的open id
     * @return 唯独消息的Json串
     */
    List<UnreadMessageDto> putUnreadMessage(String openId);

    /**
     * 只要不是当天登陆，都会进行重新获取三封信
     * @param openId 用户 open id
     * @return 结果集
     */
    @Deprecated
    List<IndexLetterDto> getLettersByOpenId(String openId);

    List<IndexLetterDto> getLettersUpgrade(String openId);

    void saveReplyFromSenderToRecipient(Reply reply);

    /**
     * @param letterReplyDto 回复信件的类
     * @return 信件何时会被送达
     */
    String replyLetter(LetterReplyDto letterReplyDto);

    Map<Integer,Integer> getNumberOfLetter(String openId);

    List<UnreadMessageDto> getAllUnreadLetter(String openId, Integer pageNum);

    @Deprecated
    LetterDto getLetterById(String letterId);

    void setLetterInitInfoByOpenId(String openId);

    LetterDto getLetterById(ReplyInfoDto replyInfoDto);

    IndexLetterDto getLetterById(IndexLetterVo indexLetterVo);

    Result<PageInfo<UnreadMessageDto>> getLetterOfHistory(String openId, Integer pageNum);

    int changeLetterState(String id, int state);
}
