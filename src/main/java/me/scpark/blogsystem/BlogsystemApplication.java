package me.scpark.blogsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Blogsystem backend 的启动类。
 *
 * <p>这个类是 Spring Boot 应用的入口：运行它就会启动内置 Tomcat、连接数据库、
 * 按实体类建表并执行 data.sql，然后把整个应用跑起来。
 *
 * <p>它所在的包 {@code me.scpark.blogsystem} 是组件扫描的起点，
 * 所以 controller / service / repository / domain / dto / config 都必须是它的子包，
 * 否则那些类不会被 Spring 扫到，接口会直接 404。
 */
@SpringBootApplication // 启用自动配置，并扫描当前包下的 Spring 组件。
public class BlogsystemApplication {

    /**
     * Java 程序的入口方法。
     *
     * @param args 启动参数，一般不用传
     */
    public static void main(String[] args) {
        // 启动 Spring Boot，同时启动内置的 Web 服务器。
        SpringApplication.run(BlogsystemApplication.class, args);
    }
}
