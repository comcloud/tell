package com.yundingxi.web.component;

import com.yundingxi.biz.service.LetterService;
import com.yundingxi.biz.service.TaskService;
import com.yundingxi.biz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version v1.0
 * @ClassName ResourceInit
 * @Author rayss
 * @Datetime 2021/3/25 8:55 下午
 */
@Component
public class ResourceInit implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(ResourceInit.class);

    private final LetterService letterService;

    private final UserService userService;

    private final TaskService taskService;

    @Autowired
    public ResourceInit(UserService userService, @Qualifier("upgradeLetterServiceImpl") LetterService letterService, TaskService taskService) {
        this.userService = userService;
        this.letterService = letterService;
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) {
        log.info("项目初始化");
        List<String> openIdList = userService.selectAllOpenId();
        letterInitForEveryOpenId(openIdList);
        stampAndAchieveInitForEveryone(openIdList);
    }

    public void letterInitForEveryOpenId(List<String> openIdList) {
        openIdList.forEach(letterService::setLetterInitInfoByOpenId);
    }

    /**
     * 邮票成就初始化，初始化内容，加载出每一个人的基本信息
     */
    public void stampAndAchieveInitForEveryone(List<String> openIdList) {
        openIdList.forEach(openId -> taskService.stampAndAchieveInitForEveryone(openId,false));
    }


}
