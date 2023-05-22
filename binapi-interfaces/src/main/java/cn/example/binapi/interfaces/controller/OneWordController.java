package cn.example.binapi.interfaces.controller;

import cn.example.binapi.common.model.entity.OneWord;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.example.binapi.sdk.constant.RequestConstant.REQUEST_REDIRECT_URL;

/**
 * 一言语录接口api请求映射Controller
 */
@Slf4j
@RestController
@RequestMapping("/oneWord")
public class OneWordController {


    /**
     * @param type hitokoto(一言)、en(中英文)、social(社会语录)、soup(毒鸡汤)、fart(彩虹屁)、zha(渣男语录)
     * @param code 选择输出格式[json|js]
     * @return 请求响应结果
     */
    @GetMapping("type")
    public String type(@RequestParam(value = "type", required = false, defaultValue = "hitokoto") String type, @RequestParam(value = "code", required = false) String code) {
        OneWord ow = new OneWord();
        return HttpRequest.get(REQUEST_REDIRECT_URL).charset(CharsetUtil.UTF_8).body(JSONUtil.toJsonStr(ow)).execute().body();
    }

    @GetMapping()
    public String randomSoulSoup(Object o) {
        log.info((String) o);
        // OneWord ow = new OneWord();
        return "www";
    }

}
