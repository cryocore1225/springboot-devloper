package me.scpark.blogsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 成员实体，对应数据库里的 members 表。
 *
 * <p>在博客系统里，members 表存的是「有权限写文章的人」—— 站内成员（作者）。
 * 文章上的 author 字段存的是作者名，不是外键，这样删掉成员也不会连带删文章。
 *
 * <p>如果以后要加登录、角色（管理员 / 普通作者），就在这张表上继续加字段：
 * 密码哈希、角色、头像地址等。
 */
@Entity // 告诉 JPA：这是一个要映射成数据表的类。
@Table(name = "members") // 表名用复数，和接口地址 /api/members 对应。
public class Member {

    /** 主键。IDENTITY 表示交给数据库自增生成，新增时不用自己填。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 成员姓名，不允许为空。 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过 50 个字符")
    @Column(nullable = false, length = 50)
    private String name;

    /** 成员邮箱，不允许为空，而且不能和其他成员重复。 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 无参构造函数。
     *
     * <p>JPA 规范要求实体必须有它，因为 Hibernate 从数据库读出数据时，
     * 需要用反射先造一个空对象再往里塞值。写成 protected 是为了避免业务代码误用这个空壳对象。
     */
    protected Member() {
    }

    /**
     * 创建新成员时使用的构造函数。
     *
     * @param name  成员姓名
     * @param email 成员邮箱
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // ---------- getter 和 setter ----------
    // Spring Boot 把对象转成 JSON、Hibernate 读写字段时都会用到它们。

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
