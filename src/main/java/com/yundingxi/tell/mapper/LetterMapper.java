package com.yundingxi.tell.mapper;

import com.yundingxi.tell.bean.entity.Letter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version v1.0
 * @ClassName LetterMapper
 * @Author rayss
 * @Datetime 2021/3/24 6:44 下午
 */

@Mapper
public interface LetterMapper {
    void insertSingleLetter(@Param("letter") Letter letter);
}
