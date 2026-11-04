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

import me.scpark.springdeveloper.domain.Member;
import me.scpark.springdeveloper.service.MemberService;

/**
 * 成员接口，对数据库里的 members 表做增删改查。
 *
 * <p>类上的 @RequestMapping("/members") 是统一前缀，
 * 下面每个方法只需要写自己那一段路径，拼起来就是完整的地址。
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * @param memberService 成员业务逻辑层
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 查询全部成员。
     *
     * @return 200 和成员列表
     */
    @GetMapping
    public ResponseEntity<List<Member>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    /**
     * 按 id 查询单个成员。
     *
     * @param id 成员编号，来自 URL 路径
     * @return 查到返回 200 和成员对象；查不到返回 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> findById(@PathVariable Long id) {
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 新增成员。
     *
     * @param member 请求体里的 JSON，@RequestBody 会自动把它转换成 Member 对象
     * @return 201 和新创建的成员（带上数据库生成的自增 id）
     */
    @PostMapping
    public ResponseEntity<Member> create(@RequestBody Member member) {
        Member created = memberService.create(member);
        // 201 表示"已创建"；Location 响应头告诉调用方这个新资源的地址。
        return ResponseEntity.created(URI.create("/members/" + created.getId())).body(created);
    }

    /**
     * 整体替换一个成员的信息（PUT）。
     * 请求体里必须提供全部字段，没提供的会被置为 null。
     *
     * @return 200 和修改后的成员；该 id 不存在返回 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @RequestBody Member member) {
        return memberService.update(id, member)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 局部更新一个成员的信息（PATCH）。
     * 只有请求体里出现的字段才会被修改，其他字段保持原样。
     *
     * @return 200 和修改后的成员；该 id 不存在返回 404
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Member> patch(@PathVariable Long id, @RequestBody Member member) {
        return memberService.patch(id, member)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 删除成员。
     *
     * @return 删除成功返回 204（表示成功但没有响应体）；该成员不存在返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (memberService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
