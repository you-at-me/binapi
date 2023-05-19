package cn.example.binapi.service;

import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.sdk.Model.Api;
import cn.example.binapi.sdk.client.RemoteCallClient;
import cn.example.binapi.service.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Carl
 * @since 2023-05-17
 */
@SpringBootTest
public class BinApiInterfaceTests {

    @Resource
    private UserService userService;

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

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    @Test
    void userRegister() {
        String userAccount = "Carl";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            userAccount = "zyshu";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            userAccount = "Carl";
            userPassword = "123456";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            userAccount = "zyshu";
            userPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            checkPassword = "123456789";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            userAccount = "zyshu";
            checkPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
            userAccount = "Carl";
            result = userService.userRegister(userAccount, userPassword, checkPassword, null);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
