package cn.example.binapi.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.example.binapi.cn.example.binapi.service.mapper")
// @EnableAspectJAutoProxy(exposeProxy=true,proxyTargetClass=true)
public class BinApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinApiServiceApplication.class, args);
    }

}
