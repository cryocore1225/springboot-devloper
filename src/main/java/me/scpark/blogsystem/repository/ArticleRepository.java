package me.scpark.blogsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import me.scpark.blogsystem.domain.Article;

/**
 * 文章的数据访问层，负责和数据库里的 articles 表打交道。
 *
 * <p>和 {@link MemberRepository} 一样，只要继承 JpaRepository 并填好泛型，
 * Spring Data JPA 就会在启动时自动生成实现类，
 * findAll、findById、save、deleteById、existsById 等方法直接就能用。
 *
 * <p>这里的查询方法分两种返回值：
 * <ul>
 *   <li>{@code List<Article>}：一次拿全部，适合「某作者的文章」「搜索结果」这类量小的场景；</li>
 *   <li>{@code Page<Article>}：分页拿，方法多一个 {@link Pageable} 参数，会额外执行一条 count 语句算总数。</li>
 * </ul>
 *
 * <p>方法名里的 {@code OrderByCreatedAtDesc} 也是派生查询的一部分：
 * 排序交给数据库做（SQL 里的 order by），不用查出来再在 Java 里 sort 一遍。
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 查询某个作者的全部文章，最新的排前面。
     *
     * <p>派生查询：findBy 后面跟实体类的字段名 Author，会被翻译成 {@code where author = ?}。
     *
     * @param author 作者名
     * @return 该作者的文章列表；一篇都没有时返回空列表
     */
    List<Article> findByAuthorOrderByCreatedAtDesc(String author);

    /**
     * 分页查询全部文章，最新的排前面。
     *
     * <p>这是博客首页最常用的查询：{@code select ... order by created_at desc limit ? offset ?}。
     *
     * @param pageable 分页参数（页码、每页条数）
     * @return 一页文章，带总条数
     */
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 按发布状态分页查询文章。
     *
     * <p>{@code findByPublished} 会被翻译成 {@code where published = ?}，
     * 传 true 查已发布、传 false 查草稿。
     *
     * @param published 是否已发布
     * @param pageable  分页参数
     * @return 一页文章
     */
    Page<Article> findByPublishedOrderByCreatedAtDesc(boolean published, Pageable pageable);

    /**
     * 按标题关键字做模糊查询，忽略大小写。
     *
     * <p>{@code Containing} 会被翻译成 {@code like %关键字%}，
     * {@code IgnoreCase} 让「spring」也能匹配「Spring Boot 入门」。
     *
     * @param keyword  标题里包含的关键字
     * @param pageable 分页参数
     * @return 一页匹配的文章；没有匹配时是空页
     */
    Page<Article> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);
}
