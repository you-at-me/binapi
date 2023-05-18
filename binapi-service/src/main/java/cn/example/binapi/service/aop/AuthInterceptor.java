package cn.example.binapi.service.aop;

import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.common.ErrorCode;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 权限校验 AOP; @Aspect 注解括号内也可带参数，即表示为该切入点对象类标识唯一组件，但是对于aop配置的切入点对象，还需要将其注入ioc容器当中，所以无需id标识
 */
@Aspect // aop配置切面对象，表示声明切面：即切面点(对要进行增强的方法配置切入点对象类)
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截，进行请求用户的权限校验，利用aop环绕增强实现权限校验
     */
    @Around("@annotation(authCheck)") // 注解方式的环绕增强，对注解所传人的参数做相应逻辑判断
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        if (Objects.isNull(user)) throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        // if (CollectionUtils.isNotEmpty(anyRole) || StringUtils.isNotBlank(mustRole)) {
        //     throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        // }

        // 执行到这里表示用户具有身份，可以访问对应接口
        // 拥有任意权限即通过：即表示当前请求的用户角色包含在任意角色里即可通过
        if (CollectionUtils.isNotEmpty(anyRole)) {
            String userRole = user.getRole();
            if (!anyRole.contains(userRole)) { // 判断当前用户是否包含在任意角色里
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 某个必需角色必须得是当前请求用户对应的用户角色才能通过
        if (StringUtils.isNotBlank(mustRole)) {
            String userRole = user.getRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行，调用 proceed 方法表示获得被增强方法的返回值，point 表示正在执行的连接点，即切点
        return joinPoint.proceed();
    }
}

