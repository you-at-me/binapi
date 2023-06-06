package cn.example.binapi.interfaces.annotation;

import java.lang.annotation.*;

/**
 * 自定义限流注解，表示多少时间内，最多只能请求访问多少次，推荐使用
 * @author Carl
 * @since 2023-05-16
 */
@Documented
@Target(ElementType.METHOD) // 表示该注解可以标记在方法上
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在运行期间保留（不会被编译器优化掉）
public @interface RateLimiter {

    /**
     * 限流阈值
     */
    long limit() default 200;

    /**
     * 限流时间窗口大小，单位毫秒
     */
    long milTimeout() default 50;
}
