package cn.example.binapi.service.aop;

import cn.example.binapi.service.annotation.RateLimiter;
import cn.example.binapi.service.exception.BusinessException;
import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.example.binapi.common.common.ResponseStatus.FLOW_TRAFFIC;

/**
 * aop 自定义注解实现限流，推荐使用，此方式跟 RateLimitAspectInterval 方法类似
 *
 * @author Carl
 * @since 2023-05-16
 */
@Component
@Aspect
public class RateLimiterAspect {

    /**
     * 存储每个接口当前时间段内已经请求的次数
     */
    private final Map<String, Long> rateMap = new ConcurrentHashMap<>();

    /**
     * 对所有包下所有使用了 RateLimiter 注解的成员方法进行限流
     *
     * @param joinPoint aop 切入点操作对象
     * @return 一般是返回要执行的方法
     */
    @Around("execution(* cn.example.binapi.service.controller..*.*(..)) && @annotation(cn.example.binapi.service.annotation.RateLimiter)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

        // 获取限流参数，限流次数以 limit 为主
        long limit = rateLimiter.limit();
        long value = rateLimiter.value();
        long milTimeout = rateLimiter.milTimeout();
        if (ObjectUtil.isAllEmpty(limit, value)) limit = 200;
        if (ObjectUtil.isNull(limit) && ObjectUtil.isNotNull(value)) limit = value;
        if (ObjectUtils.isEmpty(milTimeout)) milTimeout = 50;

        // 获取方法名和时间戳，用于存储和查询本次请求是否超过限流阈值
        String methodName = method.getName();
        long now = System.currentTimeMillis();

        synchronized (rateMap) {
            // 如果当前窗口时间内已经有请求了，那么需要检查是否超过限流阈值
            if (rateMap.containsKey(methodName)) {
                long lastRequestTime = rateMap.get(methodName); // 上次请求的时间戳
                long elapsedTime = now - lastRequestTime; // 距离上次请求的时间间隔
                if (elapsedTime < milTimeout) { // 如果时间间隔小于窗口大小，则需要检查本次请求是否超过限流阈值
                    if (rateMap.getOrDefault(methodName + "_count", 0L) >= limit) {
                        throw new BusinessException(FLOW_TRAFFIC);
                    } else {
                        rateMap.computeIfPresent(methodName + "_count", (k, v) -> v + 1);
                    }
                } else { // 否则，说明已经进入下一个时间窗口，需要重置计数器
                    rateMap.put(methodName, now);
                    rateMap.put(methodName + "_count", 1L);
                }
            } else { // 如果当前窗口时间内没有请求，说明是第一次请求，需要初始化计数器
                rateMap.put(methodName, now);
                rateMap.put(methodName + "_count", 1L);
            }
        }

        // 执行被限流的方法
        return joinPoint.proceed();
    }

}
