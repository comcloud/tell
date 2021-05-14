package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.bean.dto.LetterReplyDto;
import org.springframework.context.ApplicationEvent;

/**
 * @version v1.0
 * @ClassName PublishReplyEvent
 * @Author rayss
 * @Datetime 2021/5/14 11:38 上午
 */

public class PublishReplyEvent extends ApplicationEvent {

    private LetterReplyDto letterReplyDto;

    public PublishReplyEvent(Object source, LetterReplyDto letterReplyDto) {
        super(source);
        this.letterReplyDto = letterReplyDto;
    }

    public LetterReplyDto getLetterReplyDto() {
        return letterReplyDto;
    }
}
