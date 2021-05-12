package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.bean.dto.LetterStorageDto;
import org.springframework.context.ApplicationEvent;

/**
 * @version v1.0
 * @ClassName PublishLetterEvent
 * @Author rayss
 * @Datetime 2021/5/12 5:03 下午
 */


public class PublishLetterEvent extends ApplicationEvent {

    private final LetterStorageDto letterStorageDto;

    public PublishLetterEvent(Object source, LetterStorageDto letterStorageDto) {
        super(source);
        this.letterStorageDto = letterStorageDto;
    }

    public LetterStorageDto getLetterStorageDto(){
        return letterStorageDto;
    }
}
