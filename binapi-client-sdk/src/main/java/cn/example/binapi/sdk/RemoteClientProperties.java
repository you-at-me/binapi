package cn.example.binapi.sdk;

import cn.example.binapi.sdk.client.RemoteClientCall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ComponentScan 配置以指定的包下针对注解包的扫描，如果不指定要扫码的包，默认是以本包下进行扫描
 * @author Carl
 * @since 2023-05-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ComponentScan("cn.example.binapi.sdk")
@ConfigurationProperties(prefix = "remote.client")
public class RemoteClientProperties {
    /**
     * 通用标识，复杂、无序、无规律
     */
    private String accessKey;

    /**
     * 秘钥，复杂、无序、无规律
     */
    private String secretKey;

    /**
     * 一旦自定义的属性在其他的模块配置了值之后，就会触发这里的远程调用客户端 RemoteClientCall 类的自动装配，此时其他模块在使用的时候，只需要直接将 RemoteClientCall 类注入即可使用
     */
    @Bean
    public RemoteClientCall remoteClientCall() {
        return new RemoteClientCall(accessKey, secretKey);
    }

}
