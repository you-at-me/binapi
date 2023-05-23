package cn.example.binapi.interfaces.controller;

import cn.example.binapi.interfaces.service.MainService;
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
    private MainService mainService;

    /**
     * 请求转发
     */
    @RequestMapping("main")
    public String mainRedirect(HttpServletRequest request) {
        log.info("mainRedirect....");
        String res = mainService.mainRedirect(request);
        return StrUtil.isBlank(res) ? Constants.EMPTYSTRING : res;
    }

}
