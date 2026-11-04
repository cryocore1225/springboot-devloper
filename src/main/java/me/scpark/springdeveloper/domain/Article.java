package me.scpark.springdeveloper.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * 文章实体，对应数据库里的 articles 表。
 *
 * <p>和 {@link Member} 一样，类对应表、字段对应列；
 * 区别在于文章有正文、作者和发布时间，字段更多，也能拿来练习分页、排序和关联查询。
 */
@Entity // 告诉 JPA：这是一个要映射成数据表的类。
@Table(name = "articles") // article 在部分数据库里是保留字，表名用复数更稳妥。
public class Article {

    /** 主键，由数据库自增生成。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文章标题，不允许为空。 */
    @Column(nullable = false)
    private String title;

    /**
     * 文章正文，不允许为空。
     *
     * <p>用 @Lob 是因为正文可能很长，普通的 VARCHAR 长度有限制；
     * 加上它之后 Hibernate 会把这一列建成长文本类型（H2 里是 clob）。
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 作者，不允许为空。 */
    @Column(nullable = false)
    private String author;

    /**
     * 发布时间，不允许为空，而且创建之后不再修改。
     *
     * <p>updatable = false 表示这个字段只会出现在 insert 语句里，
     * 之后即使有人改了它，Hibernate 也不会生成 update 语句。
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 无参构造函数。
     *
     * <p>JPA 规范要求实体必须有它，Hibernate 从数据库读出数据时靠反射先造空对象再填值。
     */
    protected Article() {
    }

    /**
     * 创建新文章时使用的构造函数，发布时间取当前时刻。
     *
     * @param title   文章标题
     * @param content 文章正文
     * @param author  作者
     */
    public Article(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 保存到数据库之前自动调用。
     *
     * <p>如果经过无参构造函数造出来的对象（或者有人手动把 createdAt 置空），
     * 这里兜底补上时间，避免插入时报"非空约束"错误。
     */
    @PrePersist
    void fillCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ---------- getter 和 setter ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
