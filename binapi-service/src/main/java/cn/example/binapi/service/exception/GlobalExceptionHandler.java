package cn.example.binapi.service.exception;

import cn.example.binapi.service.common.BaseResponse;
import cn.example.binapi.service.common.ResponseStatus;
import cn.example.binapi.service.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e); // 这里的 e 打印出了所有相关异常，生产阶段建议注释掉
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ResponseStatus.SYSTEM_ERROR, e.getMessage());
    }
}
