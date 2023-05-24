package cn.example.binapi.service.exception;

import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 服务模块异常处理器
 */
@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e + e.getResponseStatus().getMessage());
        e.printStackTrace(); // 这里的 e 打印出了所有相关异常，生产阶段建议注释掉
        return ResultUtils.error(e.getResponseStatus().getCode(), e.getResponseStatus().getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException: " + e + e.getMessage());
        e.printStackTrace();
        return ResultUtils.error(ResponseStatus.SYSTEM_ERROR, e.getMessage());
    }
}
