package cn.example.binapi.sdk.constant;

import cn.hutool.core.util.StrUtil;

/**
 * @author Carl
 * @since 2023-05-17
 */
public interface MethodConstant {
    String GET = "get";

    default String toLowerCase(String compare) {
        if (!StrUtil.isLowerCase(compare)) {
            return compare.toLowerCase();
        }
        return compare;
    }

    String POST = "post";
}
