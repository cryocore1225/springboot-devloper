package me.scpark.springdeveloper.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.scpark.springdeveloper.domain.Article;
import me.scpark.springdeveloper.repository.ArticleRepository;

/**
 * 文章的业务逻辑层，增删改查的具体处理都放在这里。
 *
 * <p>结构和 {@link MemberService} 一样：控制器只负责收发 HTTP 请求，真正干什么由这一层决定。
 *
 * <p>类上的 @Transactional(readOnly = true) 表示下面的方法默认运行在只读事务里；
 * 真正要写数据的方法会各自覆盖成可写事务。
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * 通过构造函数注入 Repository。
     *
     * <p>只有一个构造函数时，Spring 会自动用它完成注入，不需要再写 @Autowired。
     *
     * @param articleRepository 文章数据访问层
     */
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 查询全部文章，最新的排在最前面。
     *
     * <p>这里用的是 Repository 里定义的派生查询，
     * 排序交给数据库做，比查出来再在 Java 里 sort 一遍更省事。
     *
     * @return 按发布时间倒序排列的文章列表；一篇都没有时返回空列表
     */
    public List<Article> findAll() {
        return articleRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 按 id 查询文章。
     *
     * @param id 文章编号
     * @return 查到的文章；不存在时返回空 Optional
     */
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * 查询某个作者的全部文章。
     *
     * @param author 作者名
     * @return 该作者的文章列表；没有时返回空列表
     */
    public List<Article> findByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }

    /**
     * 按标题关键字搜索文章，主要用于博客的搜索框。
     *
     * <p>关键字前后会做模糊匹配，传空字符串时相当于查出全部文章。
     *
     * @param keyword 标题里包含的关键字
     * @return 匹配的文章列表；没有匹配时返回空列表
     */
    public List<Article> searchByTitle(String keyword) {
        return articleRepository.findByTitleContaining(keyword);
    }

    /**
     * 新增文章。
     *
     * <p>发布时间由实体自己的构造函数填好，这里是最后一道兜底：
     * 万一是用无参构造函数造出来的对象，就在这里补上当前时间，
     * 否则插入时会因为 createdAt 非空约束直接报错。
     *
     * @param article 要新增的文章
     * @return 新增后的文章，此时已经带上数据库生成的自增 id
     */
    @Transactional
    public Article create(Article article) {
        if (article.getCreatedAt() == null) {
            article.setCreatedAt(LocalDateTime.now());
        }
        return articleRepository.save(article);
    }

    /**
     * 整体替换一篇文章的信息，对应 HTTP 的 PUT。
     *
     * <p>请求里没提供的字段会被一并覆盖成 null。
     * 发布时间不动：实体里这一列标了 updatable = false，本来就改不了。
     *
     * @param id     要修改的文章编号
     * @param source 新的文章信息
     * @return 修改后的文章；该 id 不存在时返回空 Optional
     */
    @Transactional
    public Optional<Article> update(Long id, Article source) {
        return articleRepository.findById(id).map(article -> {
            article.setTitle(source.getTitle());
            article.setContent(source.getContent());
            article.setAuthor(source.getAuthor());
            return article;
        });
        // 这里没有调用 save：查出来的 article 已经处于 Hibernate 的"托管"状态，
        // 方法结束、事务提交时，Hibernate 会自动比对字段有没有变，变了就生成 update 语句。
    }

    /**
     * 局部更新文章信息，对应 HTTP 的 PATCH，只覆盖请求里真正提供了的字段。
     *
     * @param id     要修改的文章编号
     * @param source 只填了想改的字段的文章对象
     * @return 修改后的文章；该 id 不存在时返回空 Optional
     */
    @Transactional
    public Optional<Article> patch(Long id, Article source) {
        return articleRepository.findById(id).map(article -> {
            if (source.getTitle() != null) {
                article.setTitle(source.getTitle());
            }
            if (source.getContent() != null) {
                article.setContent(source.getContent());
            }
            if (source.getAuthor() != null) {
                article.setAuthor(source.getAuthor());
            }
            return article;
        });
    }

    /**
     * 按 id 删除文章。
     *
     * @param id 要删除的文章编号
     * @return 删除成功返回 true；该 id 本来就不存在则返回 false
     */
    @Transactional
    public boolean delete(Long id) {
        if (!articleRepository.existsById(id)) {
            return false;
        }
        articleRepository.deleteById(id);
        return true;
    }
}
