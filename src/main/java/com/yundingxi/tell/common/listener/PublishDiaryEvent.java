package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.LetterStorageDto;
import javafx.application.Application;
import org.springframework.context.ApplicationEvent;

/**
 * @version v1.0
 * @ClassName PublishDiaryEvent
 * @Author rayss
 * @Datetime 2021/5/12 5:20 下午
 */

public class PublishDiaryEvent extends ApplicationEvent {

    private final DiaryDto diaryDto;

    public PublishDiaryEvent(Object source, DiaryDto diaryDto) {
        super(source);
        this.diaryDto = diaryDto;
    }

    public DiaryDto getDiaryDto(){
        return diaryDto;
    }
}
