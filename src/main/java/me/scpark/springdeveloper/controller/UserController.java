package me.scpark.springdeveloper.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.scpark.springdeveloper.entity.User;
import me.scpark.springdeveloper.service.UserService;

/**
 * 用户接口，对数据库里的 users 表做增删改查。
 *
 * <p>类上的 @RequestMapping("/users") 是统一前缀，
 * 下面每个方法只需要写自己那一段路径，拼起来就是完整的地址。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * @param userService 用户业务逻辑层
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询全部用户。
     *
     * @return 200 和用户列表
     */
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * 按 id 查询单个用户。
     *
     * @param id 用户编号，来自 URL 路径
     * @return 查到返回 200 和用户对象；查不到返回 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 新增用户。
     *
     * @param user 请求体里的 JSON，@RequestBody 会自动把它转换成 User 对象
     * @return 201 和新创建的用户（带上数据库生成的自增 id）
     */
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.create(user);
        // 201 表示"已创建"；Location 响应头告诉调用方这个新资源的地址。
        return ResponseEntity.created(URI.create("/users/" + created.getId())).body(created);
    }

    /**
     * 整体替换一个用户的信息（PUT）。
     * 请求体里必须提供全部字段，没提供的会被置为 null。
     *
     * @return 200 和修改后的用户；该 id 不存在返回 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 局部更新一个用户的信息（PATCH）。
     * 只有请求体里出现的字段才会被修改，其他字段保持原样。
     *
     * @return 200 和修改后的用户；该 id 不存在返回 404
     */
    @PatchMapping("/{id}")
    public ResponseEntity<User> patch(@PathVariable Long id, @RequestBody User user) {
        return userService.patch(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 删除用户。
     *
     * @return 删除成功返回 204（表示成功但没有响应体）；该用户不存在返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
