package com.yundingxi.tell.service;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.UserCommentVo;
import com.yundingxi.tell.util.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-20:36
 */
@Service
public interface UserService {
    Result<String> insertUser(User user);

    String getKey(String jsCode);

    Result<Object> getAllUserCommentVo(String openId);
    Result<Object> getCommNum(String openId);

    /**
     * 修改用户信息
     * @param entity User 用户实体类
     * @return
     */
    Result<String> updateUser( User entity);

    /**
     * 用户退出时更新用户最初
     * @param openId  用户OPenID
     * @return
     */
    Result<String> updateOutDate(String openId);

}
