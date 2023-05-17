package cn.example.binapi.inter.controller;

import cn.example.binapi.inter.Model.User;
import org.springframework.web.bind.annotation.*;

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
    public String getUserNameByPost(@RequestBody User user) {
        return "User, your name is" + user.getName();
    }
}
