package cn.example.binapi.interfaces.config;

import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 接口模块异常处理器
 */
@Slf4j
@RestControllerAdvice
public class InterfacesExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException: " + e.getMessage());
        e.printStackTrace();
        return ResultUtils.error(ResponseStatus.REQUEST_ERROR, e.getMessage());
    }
}
