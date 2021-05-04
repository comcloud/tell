package com.yundingxi.tell.common.datasource;

import com.yundingxi.tell.mapper.UserMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @version v1.0
 * @ClassName DataSourceAspect
 * @Author rayss
 * @Datetime 2021/5/4 2:56 下午
 */

@Aspect
@Component
public class DataSourceAspect {
    /**
     * 使用空方法定义切点表达式
     */
    @Pointcut("execution(* com.yundingxi.tell.mapper.*.*(..))")
    public void declareJointPointExpression() {
    }

    /**
     * 使用定义切点表达式的方法进行切点表达式的引入
     */
    @Before("declareJointPointExpression()")
    public void setDataSourceKey(JoinPoint point) {
        //连接点所属的类实例是ShopDao
        if (point.getTarget() instanceof UserMapper) {
            DynamicDataSourceHolder.setDataSource(DatabaseType.TELL_DB);
        } else {//连接点所属的类实例是UserDao（当然，这一步也可以不写，因为defaultTargertDataSource就是该类所用的mytestdb）
            DynamicDataSourceHolder.setDataSource(DatabaseType.BACK_DB);
        }
    }
}
