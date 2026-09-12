package me.scpark.springdeveloper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收浏览器 HTTP 请求，并返回简单的文本内容。
 *
 * <p>这里的三个接口只演示请求映射的写法，不访问数据库。
 * 真正操作数据的接口在 {@link UserController} 里。
 */
@RestController // 表示这是一个控制器，方法返回值会直接作为响应内容。
public class TestController {

    /**
     * 使用通用的 @RequestMapping 处理 GET /request 请求。
     *
     * @return 返回给浏览器的普通文本
     */
    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String requestMappingExample() {
        // @RequestMapping 可以通过 method 属性指定 HTTP 请求方法。
        return "这是 @RequestMapping 接口返回的内容。";
    }

    /**
     * 处理 GET /hi 请求。
     *
     * @return 返回给浏览器的普通文本
     */
    @GetMapping("/hi") // @GetMapping 是处理 GET 请求的简写注解。
    public String hi() {
        return "你好，这是 Spring Boot 返回的内容。";
    }

    /**
     * 处理 GET /test 请求，用来展示另一个简单接口。
     *
     * @return 返回给浏览器的普通文本
     */
    @GetMapping("/test")
    public String test() {
        return "你好，这是 /test 接口返回的内容。";
    }
}
