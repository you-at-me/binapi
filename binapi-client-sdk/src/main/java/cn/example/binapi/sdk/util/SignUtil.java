package cn.example.binapi.sdk.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 *
 * api 签名认证是一个很灵活的设计，具体要用到哪些参数、参数名如何一定要根据实际业务场景需求指定
 *
 * @author Carl
 * @since 2023-05-17
 */
public class SignUtil {

    /**
     * 加密的方式有：对称加密、非对称加密、MD5 签名(不可解密)
     * 签名生成算法 (常用的有 MD5、HMac、Sha1),  而 JWT 是一种签名认证算法
     */
    public static String getSign(String body, String secretKey) {
        Digester sha = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey;
        // String digestHex = DigestUtil.sha256Hex(secretKey); // 可进直接一步简化，参考 hutools 官网：https://hutool.cn/docs/#/crypto/%E6%91%98%E8%A6%81%E5%8A%A0%E5%AF%86-Digester
        return sha.digestHex(content);
    }
}
