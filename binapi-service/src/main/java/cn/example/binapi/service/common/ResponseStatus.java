package cn.example.binapi.service.common;

/**
 * 响应状态
 */
public enum ResponseStatus {

    SUCCESS(20000, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    TOO_LONG_ERROR(4010, "接口名称太长"),
    NOT_LOGIN(40100, "未登录"),
    NO_AUTH(40101, "无权限"),
    NOT_FOUND(40400, "请求数据不存在"),
    FORBIDDEN(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
