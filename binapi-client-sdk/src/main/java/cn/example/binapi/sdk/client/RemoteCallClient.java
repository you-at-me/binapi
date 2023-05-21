package cn.example.binapi.sdk.client;

import cn.example.binapi.sdk.Model.Api;
import cn.example.binapi.sdk.util.SignUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;

import java.util.HashMap;
import java.util.Map;

import static cn.example.binapi.sdk.constant.RequestConstant.*;

/**
 * 远程调用第三方接口的客户端，这里的第三方接口目前
 * 都是本地提供的接口，也即interfaces服务下的接口。
 *
 * @author Carl
 * @since 2023-05-17
 */
// @Data
public class RemoteCallClient {

    private Integer appId;

    private String accessKey;

    private String secretKey;

    // @Value("${alias.openapi.client.url}")
    // private final String urlPrefix = "http://localhost:9000/interfaces"; // 通过网关请求转发

    public RemoteCallClient() {
    }

    public RemoteCallClient(Integer appId, String accessKey, String secretKey) {
        this.appId = appId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getResult(Api api) {
        String json = JSONUtil.toJsonStr(api.getBody()); //body就是请求参数
        if (GET.equalsIgnoreCase(api.getMethod())) { // 所有的请求都通过main进行统一请求转发，且是通过接口网关服务进行转发到main请求路径的，防止对外暴露接口
            return makeRequest(HttpRequest.get(REQUEST_REDIRECT_URL), api, json).execute().body();
        }
        if (POST.equalsIgnoreCase(api.getMethod())) {
            return makeRequest(HttpRequest.post(REQUEST_REDIRECT_URL), api, json).execute().body();
        }
        {
            return Constants.EMPTYSTRING;
        }
    }

    private HttpRequest makeRequest(HttpRequest request, Api api, String json) {
        return request.header("Accept", "application/json;charset=UTF-8").addHeaders(getHeaders(api.getUrl(), api.getInterfaceId(), json, api.getId(), api.getAccount())).charset(CharsetUtil.UTF_8).body(json);
    }

    private Map<String, String> getHeaders(String url, String interfaceId, String body, Long userId, String account) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        Map<String, String> headers = new HashMap<>();
        headers.put("userId", String.valueOf(userId));
        headers.put("account", account);
        headers.put("interfaceId", interfaceId);
        headers.put("url", url);
        headers.put("appId", String.valueOf(appId));
        headers.put("accessKey", accessKey);// 通用标识，复杂、无序、无规律
        // headers.put("secretKey", secretKey); // 秘钥，复杂、无序、无规律; 秘钥不能直接放入请求当中在服务器之间随意传输，有可能会被拦截产生安全问题
        headers.put("body", body);
        headers.put("timeSecond", String.valueOf(System.currentTimeMillis() / 1000)); // 当前此次请求的时间戳，单位ms
        headers.put("nonce", RandomUtil.randomNumbers(4)); // nonce 随机数，用于防止重放，也就是某一个请求执行发送请求
        headers.put("sign", SignUtil.genSign(body, secretKey));  // 签名秘钥
        return headers;
    }

}
