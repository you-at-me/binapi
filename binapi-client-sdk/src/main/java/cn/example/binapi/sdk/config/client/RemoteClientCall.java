package cn.example.binapi.sdk.config.client;

import cn.example.binapi.sdk.config.Model.User;
import cn.example.binapi.sdk.config.util.SignUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口的客户端
 *
 * @author Carl
 * @since 2023-05-17
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemoteClientCall {

    private String accessKey;

    private String secretKey;

    public String getNameByGet(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.get("http://127.0.0.1:8123/api/name", paramMap);
        log.info(result);
        return result;
    }

    public String getNameByPost(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.post("http://127.0.0.1:8123/api/name", paramMap);
        log.info(result);
        return result;
    }

    public String getUsernameByPost(User user) {
        String userJson = JSONUtil.toJsonStr(user);

        HttpResponse response = HttpRequest.post("http://127.0.0.1:8123/api/name/user").addHeaders(getHeaders(userJson))  // 请求头
                .body(userJson) // 请求体
                .execute();    // 发送请求
        log.info(String.valueOf(response.getStatus()));
        String result = response.body();
        log.info(result);
        return result;
    }

    private Map<String, String> getHeaders(String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accessKey", accessKey); // 通用标识，复杂、无序、无规律
        // headers.put("secretKey", secretKey); // 秘钥，复杂、无序、无规律; 秘钥不能直接放入请求当中在服务器之间随意传输，有可能会被拦截产生安全问题
        headers.put("nonce", RandomUtil.randomNumbers(4)); // nonce 随机数，用于防止重放，也就是某一个请求执行发送请求
        headers.put("body", body); // 请求体参数，放入请求体当中只是为了做一个反推出秘钥的条件
        headers.put("timestamp", String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))); // 当前此次请求的时间戳，单位秒
        headers.put("sign", SignUtil.getSign(body, secretKey)); // 签名
        return headers;
    }

}
