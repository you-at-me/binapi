package cn.example.binapi.service.exception;

import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage()); // 这里的 e 打印出了所有相关异常，生产阶段建议注释掉
        return ResultUtils.error(e.getResponseStatus().getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException");
        return ResultUtils.error(ResponseStatus.SYSTEM_ERROR, e.getMessage());
    }
}
