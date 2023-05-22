package cn.example.binapi.interfaces.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.example.binapi.common.constant.CommonConstant.DASH;

@Slf4j
@Component
public class AcquireRequestMethodsUtils extends WebApplicationObjectSupport {

    public Map<String, String> requestMappingMap = new HashMap<>();

    /**
     * 当有请求发起的时候，这里将获取所有相关请求接口的信息
     */
    @Bean
    public void getController() {
        // 获取 WebApplicationContext，用于获取 Bean
        WebApplicationContext webApplicationContext = getWebApplicationContext();
        // 获取 spring 容器中的 RequestMappingHandlerMapping
        RequestMappingHandlerMapping requestMapping = (RequestMappingHandlerMapping) Objects.requireNonNull(webApplicationContext).getBean("requestMappingHandlerMapping");
        // 获取应用中所有的请求
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            // 1、获取所有的请求路径
            RequestMappingInfo requestMappingInfo = entry.getKey();
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            Set<String> patterns = Objects.requireNonNull(patternsCondition).getPatterns();
            // value 为处理请求信息的方法，即 code
            HandlerMethod handlerMethod = entry.getValue();
            // 2、获取类
            String type = handlerMethod.getBeanType().getName();
            // 3、获取方法
            String method = handlerMethod.getMethod().getName();
            System.out.println(patterns);
            requestMappingMap.put(patterns.toString(), type + DASH + method);
        }
        log.info("requestMappingMap: {}", requestMappingMap.toString());
    }
}
