package cn.example.binapi.service.aop;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
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
     * 对controller包及其子包下的所有方法都做切面，但不包括controller包下的UserController类
     */
    @Pointcut("execution(* cn.example.binapi.service.controller..*.*(..)) && !execution(* cn.example.binapi.service.controller.UserController.*(..))")
    public void pointcut() {
    }

    /**
     * 执行拦截，进行请求用户的权限校验，利用aop环绕增强实现权限校验，对指定包下的所有方法或者
     * 注解配置的方式进行环绕增强，注解方式的环绕增强是对注解所传人的参数做相应逻辑判断。
     */
    @Around("@annotation(cn.example.binapi.service.annotation.AuthCheck) || pointcut()") // 注解和全包名的扫描必须要协议一致，也就是当有全包名扫描时，注解不能直接注入形参变量当中。
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息，也可以使用@annotation(authCheck)将注解信息注入到形参AuthCheck authCheck中
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthCheck authCheck = method.getAnnotation(AuthCheck.class);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        if (ObjectUtil.isNotEmpty(authCheck)) {
            List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList()); // 获取注解当中配置的用户权限
            String mustRole = authCheck.mustRole();
            // 某个必需角色必须得是当前请求用户对应的用户角色才能通过
            if (StringUtils.isNotBlank(mustRole)) {
                String userRole = user.getRole();
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ResponseStatus.NO_AUTH);
                }
                return joinPoint.proceed();
            }
            // 拥有用户或管理员权限即可通过：即表示当前请求的用户角色包含在任意角色里即可通过
            if (CollectionUtils.isNotEmpty(anyRole)) {
                String userRole = user.getRole();
                if (!anyRole.contains(userRole)) { // 判断当前用户是否包含在任意角色里
                    throw new BusinessException(ResponseStatus.NO_AUTH);
                }
            }
        }
        // 通过权限校验，放行，调用 proceed 方法表示获得被增强方法的返回值，point 表示正在执行的连接点，即切点
        return joinPoint.proceed();
    }
}

