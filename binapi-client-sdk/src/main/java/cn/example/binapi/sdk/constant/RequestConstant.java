package cn.example.binapi.sdk.constant;

import cn.hutool.core.util.StrUtil;

/**
 * @author Carl
 * @since 2023-05-17
 */
public interface RequestConstant {
    String GET = "get";

    default String toLowerCase(String compare) {
        if (!StrUtil.isLowerCase(compare)) {
            return compare.toLowerCase();
        }
        return compare;
    }

    String POST = "post";

    /**
     * 网关请求路径前缀， 通过网关请求转发
     */
    String REQUEST_REDIRECT_URL = "http://localhost:9000/interfaces/main";

    /**
     * 请求地址切割字符串
     */
    String REGEX_STR = "interfaces";
}
