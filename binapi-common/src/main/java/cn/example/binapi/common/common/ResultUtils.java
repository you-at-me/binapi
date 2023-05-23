package cn.example.binapi.common.common;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResponseStatus.SUCCESS.getCode(), data, ResponseStatus.SUCCESS.getMessage());
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error() {
        return new BaseResponse<>(ResponseStatus.FORBIDDEN.getCode(), null, ResponseStatus.FORBIDDEN.getMessage());
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error(ResponseStatus errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error(ResponseStatus errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
