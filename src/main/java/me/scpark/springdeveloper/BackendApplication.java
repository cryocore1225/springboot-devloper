package me.scpark.springdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用的启动类。
 */
@SpringBootApplication // 启用自动配置，并扫描当前包下的 Spring 组件。
public class BackendApplication {

    /**
     * Java 程序的入口方法。
     */
    public static void main(String[] args) {
        // 启动 Spring Boot，同时启动内置的 Web 服务器。
        SpringApplication.run(BackendApplication.class, args);
    }
}
