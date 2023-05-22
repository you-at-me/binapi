package cn.example.binapi.interfaces.controller;

import cn.example.binapi.interfaces.service.AuthService;
import cn.hutool.core.util.StrUtil;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
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
    @RequestMapping("main")
    public String mainRedirect(HttpServletRequest request, String type) {
        log.info("mainRedirect...." + type);
        String res = authService.mainRedirect(request);
        return StrUtil.isBlank(res) ? Constants.EMPTYSTRING : res;
    }

}
