package com.yundingxi.tell.service;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.HistoryDataVo;
import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.bean.vo.ProfileVo;
import com.yundingxi.tell.util.ModelUtil;
import com.yundingxi.tell.util.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    Result<Object> getAllUserCommentVo(String openId,Integer pageNum);
//    Result<Object> getAllUserCommentVo(String openId,Integer pageNum);

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


    Result<ProfileVo> getProfile(String openId);

    /**
     * @param openId open Id
     * @return 返回样式内容，包含两块内容，所以使用了ModelUtil作为容器，第一个值是一个二维数组，这里使用集合表示，第二个是键值对
     */
    Result<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> getDataAnalysis(String openId,Long currentTimeStamp);

    Result<Integer> isTextLegal(String textContent);

    Result<HistoryDataVo> getDataOfHistory(String openId);

    Result getOfficialMsg(String openId);
}
