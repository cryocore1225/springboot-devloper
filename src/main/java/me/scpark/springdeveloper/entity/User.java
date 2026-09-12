package me.scpark.springdeveloper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 用户实体，对应数据库里的 users 表。
 *
 * <p>有了这个类，Hibernate 就知道该建一张什么样的表：
 * 类对应表，字段对应列。
 */
@Entity // 告诉 JPA：这是一个要映射成数据表的类。
@Table(name = "users") // user 是 H2 的保留字，表名直接叫 user 会建表失败，所以用复数。
public class User {

    /** 主键。IDENTITY 表示交给数据库自增生成，新增时不用自己填。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户姓名，不允许为空。 */
    @Column(nullable = false)
    private String name;

    /** 用户邮箱，不允许为空，而且不能和其他用户重复。 */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 无参构造函数。
     *
     * <p>JPA 规范要求实体必须有它，因为 Hibernate 从数据库读出数据时，
     * 需要用反射先造一个空对象再往里塞值。写成 protected 是为了避免业务代码误用这个空壳对象。
     */
    protected User() {
    }

    /**
     * 创建新用户时使用的构造函数。
     *
     * @param name  用户姓名
     * @param email 用户邮箱
     */
    public User(String name, String email) {
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
