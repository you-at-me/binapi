package cn.example.binapi.interfaces.controller;

import cn.example.binapi.interfaces.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class MainController {

    @Resource
    private AuthService authService;

    /**
     * 请求转发
     */
    @RequestMapping("/main")
    public String MainRedirect(HttpServletRequest request) {
        log.info("mainRedirect....");
        return authService.mainRedirect(request);
    }

}
