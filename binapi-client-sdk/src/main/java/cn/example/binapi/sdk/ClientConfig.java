package cn.example.binapi.sdk;

import cn.example.binapi.sdk.client.RemoteCallClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

/**
 * ComponentScan 配置以指定的包下针对注解包的扫描，如果不指定要扫码的包，默认是以本包下进行扫描
 * @author Carl
 * @since 2023-05-17
 */
@Data
@Configuration
@ConfigurationProperties("alias.openapi.client")
@ComponentScan
public class ClientConfig {

    private Integer appId;

    /**
     * 通用标识，复杂、无序、无规律
     */
    private String accessKey;

    /**
     * 秘钥，复杂、无序、无规律
     */
    private String secretKey;

    /**
     * 一旦自定义的属性在其他的模块配置了值之后，就会触发这里的远程客户端调用 RemoteCallClient
     * 类的自动装配，此时其他模块在使用的时候，只需要直接将 RemoteCallClient 类注入即可使用。
     */
    @Bean
    public RemoteCallClient apiClient() {
        RemoteCallClient client;
        try {
            Class<?> forName = Class.forName("cn.example.binapi.sdk.client.RemoteCallClient");
            Constructor<?> declaredConstructor = forName.getDeclaredConstructor(Integer.class, String.class, String.class);
            declaredConstructor.setAccessible(true);
            client = (RemoteCallClient) declaredConstructor.newInstance(appId, accessKey, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }

}

