package me.scpark.blogsystem.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 评论实体，对应数据库里的 comments 表。
 *
 * <p>和 Article 的「一对多」关系是这份代码里第一次出现的表关联：
 * 一条评论只属于一篇文章，所以用 {@code @ManyToOne}，
 * 数据库里体现为 comments 表多出一个 {@code article_id} 外键列。
 *
 * <p>{@code @JsonIgnore} 加在 article 字段上，是因为序列化成 JSON 时
 * 不需要把整篇文章再嵌进每条评论里 —— 评论是从哪篇文章查出来的，调用方本来就知道。
 * 而且文章和评论互相引用时，Jackson 会因为循环引用直接报错。
 */
@Entity
@Table(name = "comments")
public class Comment {

    /** 主键，由数据库自增生成。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评论所属的文章。
     *
     * <p>fetch = LAZY 表示查评论时先不把文章一起查出来，
     * 只有真正调用 {@code comment.getArticle()} 时才发第二条 SQL —— 这就是常说的懒加载。
     * 反序列化请求体时客户端不需要、也不应该传 article 字段，文章 id 从 URL 路径里来。
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    /** 评论人昵称，不允许为空。 */
    @NotBlank(message = "评论人不能为空")
    @Size(max = 50, message = "评论人昵称不能超过 50 个字符")
    @Column(nullable = false, length = 50)
    private String author;

    /** 评论内容，不允许为空，最长 1000 个字符。 */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过 1000 个字符")
    @Column(nullable = false, length = 1000)
    private String content;

    /** 评论时间，不允许为空，创建之后不再修改。 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 无参构造函数，JPA 规范要求实体必须有它。
     */
    protected Comment() {
    }

    /**
     * 创建新评论时使用的构造函数，评论时间取当前时刻。
     *
     * @param article 评论所属的文章
     * @param author  评论人昵称
     * @param content 评论内容
     */
    public Comment(Article article, String author, String content) {
        this.article = article;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    /** 保存前兜底补时间，避免 createdAt 非空约束报错。 */
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

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
