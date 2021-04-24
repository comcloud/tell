package com.yundingxi.tell.service.Impl;

import cn.hutool.http.HttpUtil;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.bean.vo.ProfileVo;
import com.yundingxi.tell.bean.vo.OpenIdVo;
import com.yundingxi.tell.bean.vo.UserCommentVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.ModelUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private SpittingGroovesService spittingGroovesService;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OpenIdVo openIdVo;

    @Override
    public Result<String> insertUser(User user) {

        try {
            user.setRegistrationTime(new Date());
            Integer integer = userMapper.insertUser(user);
            if (integer>0) {
                return ResultGenerator.genSuccessResult("用户注册成功!!!!!");
            } else {
                return ResultGenerator.genFailResult("注册失败！！！");
            }
        }catch (DuplicateKeyException e){
            return ResultGenerator.genSuccessResult("已经注册过了!!!!!");
        }
    }

    @Override
    public String getKey(String jsCode) {
        String baseUrl = openIdVo.getBaseUrl() + jsCode;
        String appid = openIdVo.getAppid();
        String secret = openIdVo.getSecret();
        String grantType = openIdVo.getGrantType();
        return HttpUtil.get(baseUrl + "&appid=" + appid + "&secret=" + secret + "&grant_type=" + grantType);
    }

    @Override
    public Result<Object> getAllUserCommentVo(String openId,Integer pageNum) {
        int size = 5;
        int size1 = redisUtil.lGet("comm:" + openId + ":info", 0, -1).size();
        int pageNumMax=size1/size+1;
        if (pageNum>pageNumMax){
            return ResultGenerator.genSuccessResult();
        }
        List<Object> objects = redisUtil.lGet("comm:" + openId + ":info", (pageNum-1)*size, pageNum*size-1);
        redisUtil.del("comm:" + openId + ":count");
        if (objects.isEmpty()) {
            List<UserCommentVo> userCommentVos = userMapper.getUserCommentVos(openId);
            for (UserCommentVo userCommentVo : userCommentVos) {
                redisUtil.lSet("comm:" + openId + ":info", userCommentVo);
            }
            List<Object> objects1 = redisUtil.lGet("comm:" + openId + ":info", (pageNum-1)*size, pageNum*size-1);
            return ResultGenerator.genSuccessResult(objects1);
        }
        return ResultGenerator.genSuccessResult(objects);
    }

    @Override
    public Result<Object> getCommNum(String openId) {
        Object o = redisUtil.get("comm:" + openId + ":count");
        return ResultGenerator.genSuccessResult(o);
    }

    @Override
    public Result<String> updateUser(User entity) {
        System.out.println(entity);
        if (userMapper.updateUser(entity) > 0) {
            return ResultGenerator.genSuccessResult("user 用户 信息 修改  成功!!!");
        }
        return ResultGenerator.genFailResult("更新失败!!!!!");
    }

    @Override
    public Result<String> updateOutDate(String openId) {
        if (userMapper.updateOutDate(openId) > 0) {
            return ResultGenerator.genSuccessResult("user 用户 退出  成功!!!,退出时间已经记录");
        }
        return ResultGenerator.genFailResult("最后登录时间记录失败!!!!!");
    }

    @Override
    public Result<ProfileVo> getProfile(String openId) {
        int numOfLetter = userMapper.selectNumberOfLetterByOpenId(openId);
        int numOfDiary = userMapper.selectNumberOfDiaryByOpenId(openId);
        int numOfSpit = userMapper.selectNumberOfLetSpitByOpenId(openId);
        User user = userMapper.selectNameAndUrlByOpenId(openId);

        List<ProfileNumVo> numVos = new ArrayList<>();
        numVos.add(new ProfileNumVo("解忧",numOfLetter));
        numVos.add(new ProfileNumVo("日记",numOfDiary));
        numVos.add(new ProfileNumVo("吐槽",numOfSpit));

        ProfileVo profileVo = new ProfileVo(user.getPenName(), user.getAvatarUrl(), numVos);
        return ResultGenerator.genSuccessResult(profileVo);
    }
}
