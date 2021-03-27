package com.yundingxi.tell.service;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.util.Result;
import org.springframework.stereotype.Service;

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
}
