package me.scpark.springdeveloper;

import org.springframework.web.bind.annotation.*;

/**
 * 接收浏览器 HTTP 请求，并返回简单的文本内容。
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

    /**
     * 处理 POST /users 请求，通常用于创建新数据。
     *
     * @return 创建结果
     */
    @PostMapping("/users") // @PostMapping 是处理 POST 请求的简写注解。
    public String createUser() {
        return "用户创建成功";
    }

    /**
     * 处理 PUT /users/{id} 请求，通常用于整体修改已有数据。
     *
     * @param id 要修改的用户编号
     * @return 修改结果
     */
    @PutMapping("/users/{id}") // {id} 是 URL 中的路径参数。
    public String updateUser(@PathVariable int id) {
        return "修改用户 " + id;
    }

    /**
     * 处理 PATCH /users/{id} 请求，通常用于修改已有数据的一部分。
     *
     * @param id 要修改的用户编号
     * @return 修改结果
     */
    @PatchMapping("/users/{id}") // @PatchMapping 常用于局部更新。
    public String patchUser(@PathVariable int id) {
        return "部分修改用户 " + id;
    }

    /**
     * 处理 DELETE /users/{id} 请求，通常用于删除数据。
     *
     * @param id 要删除的用户编号
     * @return 删除结果
     */
    @DeleteMapping("/users/{id}") // @DeleteMapping 是处理 DELETE 请求的简写注解。
    public String deleteUser(@PathVariable int id) {
        return "删除用户 " + id;
    }
}
