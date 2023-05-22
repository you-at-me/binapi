package cn.example.binapi.service.common;

/**
 * 响应文本信息
 */
public enum ResponseText {

    SUCCESS ("成功"),
    PARAMS_ERROR( "请求参数错误"),
    PARAMS_EMPTY("参数为空"),
    ACCOUNT_SHORT("用户账号过短"),
    ACCOUNT_LONG("用户账号过长"),
    PASSWORD_SHORT("用户密码过短"),
    PASSWORD_LONG("用户密码过长"),
    USER_NOT_EXIST("用户不存在"),
    HEADERS_ERROR( "请求头缺少"),
    TOO_LONG_ERROR("接口名称太长"),
    NOT_LOGIN("未登录"),
    NO_AUTH("无权限"),
    NOT_FOUND( "请求数据不存在"),
    FORBIDDEN("禁止访问"),
    SYSTEM_ERROR("系统内部异常"),
    OPERATION_ERROR( "操作失败"),


    INTERFACE_EMPTY("接口不存在"),
    INTERFACE_NOT_FULL("接口调用次数不足");


    /**
     * 响应信息
     */
    private final String text;

    ResponseText( String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
