package cn.example.binapi.inter.controller;

import cn.example.binapi.inter.Model.User;
import cn.example.binapi.inter.utils.SignUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Carl
 * @since 2023-05-17
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping
    public String getNameByGet(String name) {
        return "GET, your name is" + name;
    }

    @PostMapping
    public String getNameByPost(@RequestParam String name) {
        return "POST, your name is" + name;
    }

    @PostMapping("user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body"); // 放入请求体当中只是为了做一个反推出秘钥的条件


        // TODO 实际中通用标识，也就是用户名应该是从数据库当中查询出来才进行校验的
        if (!accessKey.equals("Carl")) { // 校验通用标识
            throw new RuntimeException("No Auth");
        }

        // TODO 实际上，随机数的校验应该通过判断是否与指定的值相等才能通过校验，拿在生成随机数的时候就应该通过 hashmap 或者 redis 进行存储随机数
        if (Long.parseLong(nonce) > 10000) throw new RuntimeException("No Auth");

        // TODO 时间戳的检验也要通过redis进行校验，比如当前时间不能超过发请求时间的五分钟等。

        // TODO 实际上，这个 secretKey 第二个参数应该也是从数据库当中查询得到才行
        if (!SignUtil.getSign(body, "abcdefgh").equals(sign)) {
            throw new RuntimeException("No Auth"); // 当签名算法的值不相同时表示无权限访问
        }

        return "success:: is" + user.getName();
    }
}
