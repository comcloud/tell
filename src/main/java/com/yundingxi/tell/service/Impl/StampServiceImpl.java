package com.yundingxi.tell.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.corba.se.impl.orbutil.StackImpl;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.bean.vo.StampVo;
import com.yundingxi.tell.mapper.StampMapper;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class StampServiceImpl implements StampService {
    @Autowired
    private StampMapper stampMapper;
    @Override

    public Result getAllStamp(String openId, Integer pageNum) {
        String orderBy = "sg.date desc";
        PageHelper.startPage(pageNum,10,orderBy);
        List<StampVo> stampVo = stampMapper.haveListMeAll(openId);
        PageInfo<StampVo> pageInfo = new PageInfo<>(stampVo);
        Map<String, Object> stringObjectHashMap = new HashMap<>(2);
        stringObjectHashMap.put("have",pageInfo);
        stringObjectHashMap.put("notHave",stampMapper.notHaveListMeAll(openId));
        return ResultGenerator.genSuccessResult(stringObjectHashMap);
    }
}
