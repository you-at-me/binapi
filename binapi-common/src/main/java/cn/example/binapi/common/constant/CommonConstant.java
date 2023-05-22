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
     * 请求头校验
     */
    String HEADER_NAME = "Binapi-Head";

    /**
     * 请求头值校验
     */
    String HEADER_VALUE = "But-Not-Yet";

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

}
