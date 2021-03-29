package com.yundingxi.tell.service.Impl;
import cn.hutool.http.HttpUtil;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
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
    public Result<String> insertUser(User user) {
        System.out.println(user);
        System.out.println(userMapper);
      if(userMapper.insertUser(user)>0){
          return ResultGenerator.genSuccessResult("用户注册成功!!!!!");
      }else {
          return ResultGenerator.genFailResult("注册失败！！！");
      }
    }

    @Override
    public String getKey(String jsCode) {
        String baseUrl = "https://api.weixin.qq.com/sns/jscode2session" + "?js_code=" + jsCode;
        String appid = "wx45847f8c326518ee";
        String secret = "39b3662386b4bc8ec624f814369bf205";
        String grantType = "authorization_code";
        return HttpUtil.get(baseUrl + "&appid=" + appid + "&secret=" + secret + "&grant_type=" + grantType);
    }
}
