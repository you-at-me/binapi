package cn.example.binapi.interfaces.inter;

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

    @Test
    void contextLoads() {
    }
}
