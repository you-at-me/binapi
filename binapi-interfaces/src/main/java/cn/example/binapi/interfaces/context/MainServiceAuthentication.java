package cn.example.binapi.interfaces.context;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static cn.example.binapi.common.constant.CommonConstant.APPID;

/**
 * @author Carl
 */
@Component
public class MainServiceAuthentication {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证用户的调用信息是否正确，这里直接校验主要部分APPID
     */
    public boolean isAuth(Map<String, String> headers) {
        // 大部分的请求头权限校验已经在接口网关当中校验过了
        String appId = headers.get(APPID);
        String s = stringRedisTemplate.opsForValue().get(appId);
        return !StrUtil.isBlank(s) && s.equals(appId);
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
