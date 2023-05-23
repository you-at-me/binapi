package cn.example.binapi.interfaces.config;


import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
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
        // throw new BusinessException(ResponseStatus.HEADERS_ERROR);
        return !StrUtil.isBlank(headerValue) && headerValue.equals(INTERFACE_HEADER_VALUE);
    }
}
