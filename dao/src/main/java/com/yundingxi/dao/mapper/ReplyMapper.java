package com.yundingxi.dao.mapper;

import com.yundingxi.tell.bean.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version v1.0
 * @ClassName ReplyMapper
 * @Author rayss
 * @Datetime 2021/4/12 5:24 下午
 */

@Mapper
public interface ReplyMapper {
    Reply selectReplyById(@Param("id") String id);
}
