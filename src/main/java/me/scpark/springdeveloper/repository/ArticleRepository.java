package me.scpark.springdeveloper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.scpark.springdeveloper.domain.Article;

/**
 * 文章的数据访问层，负责和数据库里的 articles 表打交道。
 *
 * <p>和 {@link MemberRepository} 一样，只要继承 JpaRepository 并填好泛型，
 * Spring Data JPA 就会在启动时自动生成实现类，
 * findAll、findById、save、deleteById、existsById 等方法直接就能用。
 *
 * <p>两个泛型参数分别是：要操作的实体类（Article）和它的主键类型（Long）。
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 查询某个作者的全部文章。
     *
     * <p>派生查询：findBy 后面跟实体类的字段名 Author，会被翻译成 {@code where author = ?}。
     *
     * @param author 作者名
     * @return 该作者的文章列表；一篇都没有时返回空列表
     */
    List<Article> findByAuthor(String author);

    /**
     * 按标题关键字做模糊查询。
     *
     * <p>Containing 会被翻译成 {@code like %关键字%}，
     * 所以传「Spring」能同时匹配「Spring Boot 入门」和「我的 Spring 笔记」。
     *
     * @param keyword 标题里包含的关键字
     * @return 匹配的文章列表；没有匹配时返回空列表
     */
    List<Article> findByTitleContaining(String keyword);

    /**
     * 查询全部文章，按发布时间倒序排列（最新的排在最前面）。
     *
     * <p>方法名里的 OrderBy 也是派生查询的一部分：CreatedAt 是实体的字段，
     * Desc 表示降序。写博客列表接口时基本都会用到它。
     *
     * @return 按发布时间倒序排列的文章列表
     */
    List<Article> findAllByOrderByCreatedAtDesc();
}
