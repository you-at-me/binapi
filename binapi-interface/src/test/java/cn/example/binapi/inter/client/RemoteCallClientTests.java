package cn.example.binapi.inter.client;

import cn.example.binapi.inter.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Carl
 * @since 2023-05-17
 */
@SpringBootTest
public class RemoteCallClientTests {

    @Test
    void callTest() {
        RemoteCallClient call = new RemoteCallClient("Carl", "abcdefgh");
        // System.out.println(call.getNameByGet("Kite"));
        // System.out.println(call.getNameByPost("post"));
        System.out.println(call.getUsernameByPost(new User("Carl")));
    }
}
