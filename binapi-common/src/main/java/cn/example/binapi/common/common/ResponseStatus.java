package cn.example.binapi.common.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 响应状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseStatus {

    SUCCESS(20000, "ok"),
    INTERFACE_PURCHASE_SUCCESS(20010,"接口购买成功"),

    PARAMS_ERROR(40000, "请求参数错误"),
    PARAMS_CONTENT_ERROR(40001, "请求内容错误"),
    PARAMS_NOT_COMPLIANT(40002, "年龄不符合要求"),
    PARAMS_GENDER_COMPLIANT(40003, "性别不符合要求"),
    HEADERS_ERROR( 40004,"请求头缺少"),
    PARAMS_TOO_LONG(40005, "名称过长"),
    PARAMS_EMPTY(40006, "参数为空"),
    ACCOUNT_ERROR(40007, "账号错误"),
    PASSWORD_ERROR(40008, "密码错误"),
    ACCOUNT_SHORT(40009, "用户账号过短"),
    ACCOUNT_LONG(40010, "用户账号过长"),
    PASSWORD_SHORT(40011, "用户密码过短"),
    PASSWORD_LONG(40012, "用户密码过长"),
    PASSWORD_NOT_COMPLIANT(40013, "两次输入的密码不一致"),
    ACCOUNT_EXIST(40014, "账号重复"),
    USER_NOT_EXIST(40015,"用户不存在"),
    NOT_ERROR(40016, "用户不存在或密码错误"),

    TOO_LONG_ERROR(40100, "接口名称太长"),
    INTERFACE_CLOSURE(40101,"接口已关闭"),
    INTERFACE_NOT_USED(40102, "接口不可用"),
    INTERFACE_EMPTY(40103,"接口不存在"),
    INTERFACE_NOT_FULL(40104,"接口调用次数不足"),
    NOT_EXIST(40105, "接口或用户不存在"),
    COUNT_NOT_FULL(40106, "调用次数不足"),

    NOT_LOGIN(40200, "未登录"),
    NO_AUTH(40201, "无权限"),

    FORBIDDEN(40300, "禁止访问"),

    NOT_FOUND(40400, "请求数据不存在"),

    SYSTEM_ERROR(50000, "系统内部异常"),
    REGISTER_ERROR(50001, "注册失败，数据库错误"),
    OPERATION_ERROR(50002, "操作失败");

    /**
     * 状态码
     */
    public int code;

    /**
     * 信息
     */
    public String message;

}
