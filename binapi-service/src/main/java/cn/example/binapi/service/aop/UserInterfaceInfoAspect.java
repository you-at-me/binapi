package cn.example.binapi.service.aop;

import cn.example.binapi.service.service.UserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新初始化用户调用接口次数
 */
@Slf4j
@Aspect
@Component
public class UserInterfaceInfoAspect {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @AfterReturning("@annotation(cn.example.binapi.service.annotation.InitUserInterfaceCount)")
    public void afterUserInterfaceInfoChanged(JoinPoint joinPoint) {
        // Object[] args = joinPoint.getArgs();
        // if (args != null && args.length > 0) {
        //     for (Object arg : args) {
        //         if (arg instanceof Long) {
        //             userInterfaceInfoService.addUserInterfaceInfo();
        //         }
        //     }
        // }
        userInterfaceInfoService.addUserInterfaceInfo();
    }
}

