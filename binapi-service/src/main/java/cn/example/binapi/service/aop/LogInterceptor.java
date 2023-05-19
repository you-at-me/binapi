package cn.example.binapi.service.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 **/
@Aspect // aop配置切面对象，表示声明切面：即切面点(对要进行增强的方法配置切入点对象类)
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截，打印出相应请求的日志信息
     */
    @Around("execution(* cn.example.binapi.service.controller.*.*(..))") // 对目标要增强的方法进行环绕增强
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        System.out.printf("******request start，id: %s, path: %s, ip: %s, params: %s%n", requestId, url, httpServletRequest.getRemoteHost(), reqParam);
        // 执行原方法，获得被增强方法的返回值，point 表示正在执行的连接点，即切点
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        System.out.printf("******request end, id: %s, cost: %sms %n", requestId, totalTimeMillis);
        return result;
    }
}

