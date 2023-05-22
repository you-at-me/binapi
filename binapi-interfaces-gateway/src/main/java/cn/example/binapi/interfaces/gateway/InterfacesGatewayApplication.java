package cn.example.binapi.interfaces.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Carl
 * @since 2023-05-20
 */
@EnableDubbo
@SpringBootApplication
public class InterfacesGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterfacesGatewayApplication.class, args);
    }
}
