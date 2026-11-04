package me.scpark.blogsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.dto.PageResult;
import me.scpark.blogsystem.repository.ArticleRepository;
import me.scpark.blogsystem.repository.CommentRepository;

/**
 * 文章的业务逻辑层，增删改查、分页和搜索的具体处理都放在这里。
 *
 * <p>结构和 {@link MemberService} 一样：控制器只负责收发 HTTP 请求，真正干什么由这一层决定。
 *
 * <p>类上的 @Transactional(readOnly = true) 表示下面的方法默认运行在只读事务里；
 * 真正要写数据的方法会各自覆盖成可写事务。
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    /** 每页最多返回多少条，防止有人传 {@code size=1000000} 把数据库拖垮。 */
    private static final int MAX_PAGE_SIZE = 50;

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    /**
     * 通过构造函数注入两个 Repository。
     *
     * <p>只有一个构造函数时，Spring 会自动用它完成注入，不需要再写 @Autowired。
     *
     * @param articleRepository 文章数据访问层
     * @param commentRepository 评论数据访问层，删文章时要顺带清掉它的评论
     */
    public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 分页查询文章，最新的排在最前面。
     *
     * @param page      页码，从 0 开始；传负数按 0 处理
     * @param size      每页条数；超出范围会被夹到 1～50
     * @param published 只看已发布(true) / 只看草稿(false) / 全部都看(null)
     * @return 一页文章和分页信息
     */
    public PageResult<Article> findPage(int page, int size, Boolean published) {
        Pageable pageable = toPageable(page, size);
        Page<Article> result = (published == null)
                ? articleRepository.findAllByOrderByCreatedAtDesc(pageable)
                : articleRepository.findByPublishedOrderByCreatedAtDesc(published, pageable);
        return PageResult.of(result);
    }

    /**
     * 按标题关键字搜索文章，主要用于博客的搜索框。
     *
     * <p>关键字前后会做模糊匹配，并且忽略大小写。
     * 传空字符串等于「不筛选」，直接返回第一页全部文章。
     *
     * @param keyword 标题里包含的关键字
     * @param page    页码，从 0 开始
     * @param size    每页条数
     * @return 一页匹配的文章
     */
    public PageResult<Article> searchByTitle(String keyword, int page, int size) {
        if (keyword == null || keyword.isBlank()) {
            return findPage(page, size, null);
        }
        return PageResult.of(articleRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword.trim(), toPageable(page, size)));
    }

    /**
     * 查询某个作者的全部文章，最新的排前面。
     *
     * @param author 作者名
     * @return 该作者的文章列表；没有时返回空列表
     */
    public List<Article> findByAuthor(String author) {
        return articleRepository.findByAuthorOrderByCreatedAtDesc(author);
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
     * <p>请求里没提供的字段会被一并覆盖成 null（published 除外，它是基本类型，
     * 不传时按 false 处理，这点在 README 的接口说明里有写）。
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
            article.setTags(source.getTags());
            article.setPublished(source.isPublished());
            return article;
        });
        // 这里没有调用 save：查出来的 article 已经处于 Hibernate 的"托管"状态，
        // 方法结束、事务提交时，Hibernate 会自动比对字段有没有变，变了就生成 update 语句。
    }

    /**
     * 局部更新文章信息，对应 HTTP 的 PATCH，只覆盖请求里真正提供了的字段。
     *
     * <p>发布状态不在这里改，用 {@link #setPublished(Long, boolean)} 单独切换。
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
            if (source.getTags() != null) {
                article.setTags(source.getTags());
            }
            return article;
        });
    }

    /**
     * 切换文章的发布状态：发布草稿，或者把已发布文章撤成草稿。
     *
     * @param id        文章编号
     * @param published true 表示发布，false 表示撤回成草稿
     * @return 修改后的文章；该 id 不存在时返回空 Optional
     */
    @Transactional
    public Optional<Article> setPublished(Long id, boolean published) {
        return articleRepository.findById(id).map(article -> {
            article.setPublished(published);
            return article;
        });
    }

    /**
     * 按 id 删除文章。
     *
     * <p>删除前先把这个 id 下的评论一起删掉：comments.article_id 是非空外键，
     * 留着孤儿评论会让删除文章的操作直接失败。
     *
     * @param id 要删除的文章编号
     * @return 删除成功返回 true；该 id 本来就不存在则返回 false
     */
    @Transactional
    public boolean delete(Long id) {
        if (!articleRepository.existsById(id)) {
            return false;
        }
        commentRepository.deleteByArticle_Id(id);
        articleRepository.deleteById(id);
        return true;
    }

    /**
     * 把接口传进来的页码和条数整理成合法的分页参数。
     *
     * @param page 页码，从 0 开始
     * @param size 每页条数
     * @return 合法的 Pageable
     */
    private static Pageable toPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize);
    }
}
