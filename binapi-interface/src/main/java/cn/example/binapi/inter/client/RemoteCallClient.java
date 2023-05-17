package cn.example.binapi.inter.client;

import cn.example.binapi.inter.Model.User;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * 调用第三方接口的客户端
 *
 * @author Carl
 * @since 2023-05-17
 */
@Slf4j
public class RemoteCallClient {

    public String getNameByGet(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.get("http://127.0.0.1:8123/api/name", paramMap);
        log.info(result);
        return result;
    }

    public String getNameByPost(@RequestParam String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.post("http://127.0.0.1:8123/api/name", paramMap);
        log.info(result);
        return result;
    }

    public String getUsernameByPost(@RequestBody User user) {
        String userJson = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post("http://127.0.0.1:8123/api/name/user").body(userJson).execute();
        log.info(String.valueOf(response.getStatus()));
        String result = response.body();
        log.info(result);
        return result;
    }
}
