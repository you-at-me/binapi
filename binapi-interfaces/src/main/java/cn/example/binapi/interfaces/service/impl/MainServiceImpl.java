package cn.example.binapi.interfaces.service.impl;


import cn.example.binapi.interfaces.context.AcquireRequestMethods;
import cn.example.binapi.interfaces.context.MainServiceAuthentication;
import cn.example.binapi.interfaces.service.MainService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

import static cn.example.binapi.common.constant.CommonConstant.DASH;
import static cn.example.binapi.sdk.constant.RequestConstant.REGEX_STR;

@Slf4j
@Service
public class MainServiceImpl  implements MainService {

    @Resource
    private MainServiceAuthentication mainServiceAuthentication;

    @Resource
    private AcquireRequestMethods acquire;

    @Resource
    private ApplicationContext context;

    @Override
    public String mainRedirect(HttpServletRequest request) {
        Map<String, String> headers = mainServiceAuthentication.getHeaders(request);
        // 验证请求参数和密钥等是否合法
        boolean isAuth = mainServiceAuthentication.isAuth(headers);
        if (!isAuth) return null;
        // 接口有权限调用时，则改为根据实际请求的测试地址来进行调用，而并非写死
        // 1、获取当前服务请求映射路径中的所有请求访问类名和方法，都是以短路径url地址，例如：[/main]
        Map<String, String> requestMappingMap = acquire.requestMappingMap;
        String url = headers.get("url");
        String[] urlSplit = url.split(REGEX_STR);
        String key = "[" + urlSplit[urlSplit.length - 1] + "]";
        String typeAndMethod = requestMappingMap.get(key);
        log.info("url: {}", url);
        log.info("key: {}", key);
        log.info("res: {}", typeAndMethod);
        if (StrUtil.isBlank(typeAndMethod)) {
            log.error("AuthService...res is null");
            return null;
        }
        String[] split = typeAndMethod.split(DASH);
        Object res;
        try {
            // 通过反射构造
            Class<?> clazz = Class.forName(split[0]);
            // 这里规定所有的请求路径映射的方法参数都是Object类型的，所以在每个请求路径参数接收的时候，必须使用Object类型进行参数的接收，而且由于是object对象，实例化对象需要从容器中拿到
            Method classMethod = clazz.getMethod(split[1], Object.class);
            log.info("classMethod: {}", classMethod);
            // 调用执行对应的请求映射路径方法
            res = classMethod.invoke(context.getBean(clazz), headers.get("body"));
        } catch (Exception e) {
            log.info("error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return String.valueOf(res);
    }
}
