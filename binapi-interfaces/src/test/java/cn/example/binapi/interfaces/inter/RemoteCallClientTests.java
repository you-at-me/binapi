package cn.example.binapi.interfaces.inter;

import cn.example.binapi.sdk.Model.Api;
import cn.example.binapi.sdk.client.RemoteCallClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Carl
 * @since 2023-05-17
 */
@SpringBootTest
public class RemoteCallClientTests {

    @Resource
    private RemoteCallClient client;

    /**
     * 在 application.yml 文件下配置好了自定义的属性之后，这里就可以进行测试了。因为一旦配置了之后，就会触发远程调用客户端 RemoteClientCall 类的自动装配，此时我们只需要直接注入使用即可。
     */
    @Test
    void contextLoads() {
        Api api = new Api();
        api.setBody("Kite");
        String result = client.getResult(api);
        System.out.println("Kite".equals(result));
    }
}
