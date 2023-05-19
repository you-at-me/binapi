package cn.example.binapi.common.constant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 用户登录态键，session 存储的 key 键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 用户在 redis 的过期时间
     */
    long USER_LOGIN_EXPIRE_TIME = 24 * 60 * 60;


    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    // endregion
}
