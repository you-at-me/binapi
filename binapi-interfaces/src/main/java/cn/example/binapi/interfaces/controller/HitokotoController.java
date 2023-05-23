package cn.example.binapi.interfaces.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Carl
 * @since 2023-05-23
 */
@Slf4j
@RestController
@RequestMapping("/hitokoto")
public class HitokotoController {

    private final String URL = "https://v1.hitokoto.cn/?c=f&encode=text";

    @GetMapping("get")
    public String randomSoulSoup(Object o) {
        log.info(o.toString());
        return HttpRequest.get(URL).charset(CharsetUtil.UTF_8).execute().body();
    }
}
