package cn.example.binapi.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * maxInactiveIntervalInSeconds: 表示会话超时（以秒为单位）。默认情况下它设置为 1800 秒（30 分钟）
 *
 * @author Carl
 * @since 2023-05-19
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600, redisNamespace = "binapi:spring:session")
public class RedisSessionConfig {

    /**
     * session会话存入redis的序列化方式
     */
    @Bean
    public StringRedisSerializer springSessionDefaultRedisSerializer() {
        return new StringRedisSerializer();
    }
}