package cn.example.binapi.interfaces.service.impl;


import cn.example.binapi.common.model.entity.Auth;
import cn.example.binapi.interfaces.mapper.AuthMapper;
import cn.example.binapi.interfaces.service.AuthService;
import cn.example.binapi.interfaces.util.AcquireRequestMethodsUtils;
import cn.example.binapi.interfaces.util.AuthUtils;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService {

    @Resource
    private AuthUtils authUtils;

    @Resource
    private AcquireRequestMethodsUtils acquire;

    @Resource
    private ApplicationContext context;

    @Override
    public String mainRedirect(HttpServletRequest request) {
        Map<String, String> headers = authUtils.getHeaders(request);
        // 验证请求参数和密钥等是否合法
        boolean isAuth = authUtils.isAuth(headers);
        if (isAuth) { // 接口有权限调用时，则改为根据实际请求的测试地址来进行调用，而并非写死
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
                Class<?> forName = Class.forName(split[0]);
                // 由于是object对象，所以实例化对象需要从容器中拿到
                Method classMethod = forName.getMethod(split[1], Object.class);
                log.info("classMethod: {}", classMethod);
                // 调用执行对应的请求映射路径方法
                res = classMethod.invoke(context.getBean(forName), headers.get("body"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return String.valueOf(res);
        }
        return null;
    }
}
