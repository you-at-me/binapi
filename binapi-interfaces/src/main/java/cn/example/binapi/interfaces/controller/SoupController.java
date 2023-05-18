package cn.example.binapi.interfaces.controller;

import cn.example.binapi.interfaces.service.SoulSoupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 心灵鸡汤API
 */
@RestController
@RequestMapping("/soulSoup")
public class SoupController {

    @Resource
    private SoulSoupService soulSoupService;

    @GetMapping("random")
    public String randomSoulSoup(Object object) {
        return soulSoupService.getRandom();
    }
}
