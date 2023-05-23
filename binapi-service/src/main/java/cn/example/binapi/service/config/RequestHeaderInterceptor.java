package cn.example.binapi.service.config;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.service.exception.BusinessException;
import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.example.binapi.common.constant.CommonConstant.HEADER_NAME;
import static cn.example.binapi.common.constant.CommonConstant.HEADER_VALUE;

/**
 * @author Carl
 * @since 2023-05-21
 */
// @Component
public class RequestHeaderInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String headerValue = request.getHeader(HEADER_NAME);
        if (StrUtil.isBlank(headerValue) || !HEADER_VALUE.equals(headerValue)) {
            throw new BusinessException(ResponseStatus.HEADERS_ERROR);
        }
        return true;
    }
}
