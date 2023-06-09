package cn.example.binapi.interfaces.controller;

import cn.example.binapi.common.model.entity.User;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 名称 API
 *
 */
@Slf4j
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("get")
    public String getNameByGet(Object name) throws UnsupportedEncodingException {
        log.info("getNameByGet...");
        byte[] bytes = name.toString().getBytes("iso8859-1");
        name = new String(bytes, StandardCharsets.UTF_8);
        return "(GET) 你的名字是" + name;
    }

    @PostMapping("post")
    public String getNameByPost(Object o) {
        return "(POST) 你的名字是::" + o.toString();
    }

    @PostMapping("user")
    public String getUsernameByPost(Object object) {
        log.info("name...object: {}", object);
        Gson gson = new Gson();
        User user = gson.fromJson(object.toString(), User.class);
        return "(POST) 用户名字是" + user.getUsername();
    }
}
