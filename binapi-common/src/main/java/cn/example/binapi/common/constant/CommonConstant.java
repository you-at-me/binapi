package cn.example.binapi.common.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "descend";

    /**
     * 盐值，混淆密码
     */
    String SALT = "binapi";

    /**
     * 接口可用次数
     */
    String COUNT_EMPTY = "调用次数不足";

    /**
     * 全局网关请求头校验
     */
    String HEADER_NAME = "Binapi-Head";

    /**
     * 全局网关请求头值校验
     */
    String HEADER_VALUE = "But-Not-Yet";

    /**
     * 接口网关请求头校验
     */
    String INTERFACE_HEADER_NAME = "Binapi-Interface";

    /**
     * 接口网关请求头值校验
     */
    String INTERFACE_HEADER_VALUE = "Yet-Not-But";

    /**
     * 请求映射方法短横
     */
    String DASH = "-";

    /**
     * appId
     */
    String APPID = "appId";

    /**
     * appId
     */
    Integer APPID_EXPIRE = 60;

    String INTERFACE_PURCHASE_SUCCESS = "接口购买成功";

    String INTERFACE_PURCHASE_FAILED = "接口购买失败";

    String INTERFACE_CALL_FAILED = "接口远程调用失败";

    String INTERFACE_NOT_FULL = "接口调用次数不足";
}
