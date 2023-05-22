package cn.example.binapi.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDubbo
// @EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class BinApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinApiServiceApplication.class, args);
    }

}
