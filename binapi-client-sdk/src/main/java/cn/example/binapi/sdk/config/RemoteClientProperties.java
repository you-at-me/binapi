package cn.example.binapi.sdk.config;

import cn.example.binapi.sdk.config.client.RemoteClientCall;
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

    @Bean
    public RemoteClientCall remoteClientCall() {
        return new RemoteClientCall(accessKey, secretKey);
    }

}
