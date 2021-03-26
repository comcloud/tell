package com.yundingxi.tell.service.Impl;

import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.UserService;
import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-20:37
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public Integer insertUser(User user) {

      return   userMapper.insertUser(user);
    }

    public static void main(String[] args) {
        User user = new User();
        new UserService() {
            @Override
            public Integer insertUser(User user) {
                return null;
            }
        }.insertUser(user);
    }
}
