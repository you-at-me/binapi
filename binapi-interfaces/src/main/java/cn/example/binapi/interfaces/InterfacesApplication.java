package cn.example.binapi.interfaces;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Carl
 * @since 2023-05-17
 */
@SpringBootApplication
@MapperScan("cn.example.binapi.interfaces.mapper")
public class InterfacesApplication {
    public static void main(String[] args) {
        System.out.println("Interfaces Application started...");
        SpringApplication.run(InterfacesApplication.class, args);
    }
}
