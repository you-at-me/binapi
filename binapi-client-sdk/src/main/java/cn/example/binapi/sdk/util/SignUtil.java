package cn.example.binapi.sdk.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import lombok.extern.slf4j.Slf4j;

/**
 * 签名工具: api 签名认证是一个很灵活的设计，具体要用到哪些参数、参数名如何一定要根据实际业务场景需求指定
 *
 * @author Carl
 * @since 2023-05-17
 */
@Slf4j
public class SignUtil {
    /**
     * 生成签名: 加密的方式有：对称加密、非对称加密、MD5 签名(不可解密)
     * 签名生成算法 (常用的有 MD5、HMac、Sha1),  而 JWT 是一种签名认证算法
     */
    public static String genSign(String body, String secretKey) {
        Digester sha = new Digester(DigestAlgorithm.SHA256);
        String content;
        if (StrUtil.isAllNotBlank(body)) {
            content = body + secretKey;
        } else {
            content = "blank_value" + secretKey;
        }
        log.info("generate_Sign...body: {}", body);
        log.info("generate_Sign...content: {}", content);
        return sha.digestHex(content);
    }
}
