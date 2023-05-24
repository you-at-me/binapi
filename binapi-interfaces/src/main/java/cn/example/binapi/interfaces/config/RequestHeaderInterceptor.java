package cn.example.binapi.interfaces.config;


import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.example.binapi.common.constant.CommonConstant.INTERFACE_HEADER_NAME;
import static cn.example.binapi.common.constant.CommonConstant.INTERFACE_HEADER_VALUE;

/**
 * @author Carl
 * @since 2023-05-21
 */
// @Component
public class RequestHeaderInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String headerValue = request.getHeader(INTERFACE_HEADER_NAME);
        if (StrUtil.isBlank(headerValue) || !INTERFACE_HEADER_VALUE.equals(headerValue)) {
            throw new RuntimeException(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
            // return false;
        }
        return true;
    }
}
