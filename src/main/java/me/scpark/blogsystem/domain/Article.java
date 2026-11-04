package me.scpark.blogsystem.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 文章实体，对应数据库里的 articles 表。
 *
 * <p>这是整个博客系统的核心表：标题、正文、作者、标签、是否已发布，
 * 外加创建时间和最后修改时间。评论通过 {@link Comment#getArticle()} 挂在文章下面。
 *
 * <p>字段上的 {@code @NotBlank} 不会在存库时自动生效，
 * 必须由控制器参数上的 {@code @Valid} 触发校验，不合法时会直接返回 400。
 */
@Entity // 告诉 JPA：这是一个要映射成数据表的类。
@Table(name = "articles") // article 在部分数据库里是保留字，表名用复数更稳妥。
public class Article {

    /** 主键，由数据库自增生成。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文章标题，不允许为空，最长 200 个字符。 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过 200 个字符")
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 文章正文，不允许为空。
     *
     * <p>用 @Lob 是因为正文可能很长，普通的 VARCHAR 长度有限制；
     * 加上它之后 Hibernate 会把这一列建成长文本类型（H2 里是 clob）。
     */
    @Lob
    @NotBlank(message = "正文不能为空")
    @Column(nullable = false)
    private String content;

    /** 作者名，不允许为空。 */
    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者名不能超过 50 个字符")
    @Column(nullable = false, length = 50)
    private String author;

    /**
     * 标签，多个标签用英文逗号分隔，例如 {@code "Spring,JPA,后端"}。
     *
     * <p>这里是刻意保持简单的做法：博客的标签量很小，
     * 拆成独立的 tags / article_tags 两张表会增加不少代码，等确实需要按标签检索时再拆。
     */
    @Size(max = 200, message = "标签总长度不能超过 200 个字符")
    @Column(length = 200)
    private String tags;

    /**
     * 是否已发布。草稿为 false，草稿不会出现在公开列表里。
     *
     * <p>新建文章时默认是 true；想存草稿就在请求体里传 {@code "published": false}。
     */
    @Column(nullable = false)
    private boolean published = true;

    /**
     * 发布时间，不允许为空，而且创建之后不再修改。
     *
     * <p>updatable = false 表示这个字段只会出现在 insert 语句里，
     * 之后即使有人改了它，Hibernate 也不会生成 update 语句。
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，每次更新文章时由 {@link #touchUpdatedAt()} 自动刷新。 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
        this.updatedAt = this.createdAt;
    }

    /**
     * 保存到数据库之前自动调用。
     *
     * <p>如果经过无参构造函数造出来的对象（或者有人手动把时间置空），
     * 这里兜底补上时间，避免插入时报"非空约束"错误。
     */
    @PrePersist
    void fillTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    /**
     * 更新之前自动调用：只要文章被修改，最后修改时间就刷新成当前时刻。
     *
     * <p>注意更新后返回给调用方的 JSON 里，updatedAt 是数据库那边算好的新值，
     * 不是请求里传进来的值 —— 这个字段由后端说了算。
     */
    @PreUpdate
    void touchUpdatedAt() {
        updatedAt = LocalDateTime.now();
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
