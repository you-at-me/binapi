package cn.example.binapi.interfaces.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Carl
 */
@Component
public class AuthUtils {

    /**
     * 验证用户的调用信息是否正确
     */
    public boolean isAuth(Map<String, String> headers) {
        // TODO isAuth

        // String accessKey = headers.get("accessKey");
        // String nonce = headers.get("nonce");
        // String timeSecond = headers.get("timeSecond");
        // String sign = headers.get("sign");
        // String body = headers.get("body"); // 放入请求体当中只是为了做一个反推出秘钥的条件
        //
        //
        // // TODO 实际中通用标识，也就是用户名应该是从数据库当中查询出来才进行校验的
        // if (!accessKey.equals("Carl")) { // 校验通用标识
        //     throw new RuntimeException("No Auth");
        // }
        //
        // // TODO 实际上，随机数的校验应该通过判断是否与指定的值相等才能通过校验，拿在生成随机数的时候就应该通过 hashmap 或者 redis 进行存储随机数
        // if (Long.parseLong(nonce) > 10000) throw new RuntimeException("No Auth");
        //
        // // TODO 时间戳的检验也要通过redis进行校验，比如当前时间不能超过发请求时间的五分钟等。
        //
        // // TODO 实际上，这个 secretKey 第二个参数应该也是从数据库当中查询得到才行
        // if (!SignUtil.getSign(body, "abcdefgh").equals(sign)) {
        //     throw new RuntimeException("No Auth"); // 当签名算法的值不相同时表示无权限访问
        // }

        return true;
    }

    /**
     * 获取请求头中的信息
     */
    public Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", request.getHeader("userId"));
        map.put("account", request.getHeader("account"));
        map.put("interfaceId", request.getHeader("interfaceId"));
        map.put("url", request.getHeader("url"));
        map.put("appId", request.getHeader("appId"));
        map.put("accessKey", request.getHeader("accessKey"));
        // map.put("secretKey", request.getHeader("secretKey"));
        map.put("body", request.getHeader("body"));
        map.put("timeSecond", request.getHeader("timeSecond"));
        map.put("nonce", request.getHeader("nonce"));
        map.put("sign", request.getHeader("sign"));
        System.out.println("interfaces: map::" + map);
        return map;
    }
}
