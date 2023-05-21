package cn.example.binapi.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Carl
 * @since 2023-05-20
 */
@EnableDubbo
@SpringBootApplication
public class BinApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(BinApiGatewayApplication.class, args);
    }
}
