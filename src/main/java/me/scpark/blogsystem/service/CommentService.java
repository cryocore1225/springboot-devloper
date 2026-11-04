package me.scpark.blogsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.scpark.blogsystem.domain.Comment;
import me.scpark.blogsystem.repository.ArticleRepository;
import me.scpark.blogsystem.repository.CommentRepository;

/**
 * 评论的业务逻辑层。
 *
 * <p>这里多了一个平时容易忽略的判断：创建评论之前必须先确认文章存在。
 * 如果直接拿着一个不存在的 articleId 去 insert，数据库会因为外键约束抛出异常，
 * 最后变成 500 —— 但那明明是「文章不存在」，应该是 404。
 * 所以这件事放在 Service 层做，控制器只管把结果翻译成状态码。
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    /**
     * @param commentRepository 评论数据访问层
     * @param articleRepository 文章数据访问层，用来判断评论要挂的文章是否存在
     */
    public CommentService(CommentRepository commentRepository, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * 查询某篇文章下的全部评论，按时间正序。
     *
     * <p>返回值套了两层 Optional：外层表示「文章在不在」，内层才是评论列表。
     * 这样控制器就能区分「文章不存在 → 404」和「文章存在但没有评论 → 200 + []」，
     * 不会把这两种情况混成一个空的 200。
     *
     * @param articleId 文章编号
     * @return 文章存在时返回评论列表（可能为空列表）；文章不存在时返回空 Optional
     */
    public Optional<List<Comment>> findByArticleId(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            return Optional.empty();
        }
        return Optional.of(commentRepository.findByArticle_IdOrderByCreatedAtAsc(articleId));
    }

    /**
     * 按 id 查询单条评论。
     *
     * @param id 评论编号
     * @return 查到的评论；不存在时返回空 Optional
     */
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * 给某篇文章新增评论。
     *
     * @param articleId 文章编号
     * @param source    请求体里的评论内容，只需要 author 和 content
     * @return 新增后的评论；文章不存在时返回空 Optional
     */
    @Transactional
    public Optional<Comment> create(Long articleId, Comment source) {
        return articleRepository.findById(articleId).map(article -> {
            Comment comment = new Comment(article, source.getAuthor(), source.getContent());
            return commentRepository.save(comment);
        });
    }

    /**
     * 按 id 删除评论。
     *
     * @param id 评论编号
     * @return 删除成功返回 true；该评论本来就不存在则返回 false
     */
    @Transactional
    public boolean delete(Long id) {
        if (!commentRepository.existsById(id)) {
            return false;
        }
        commentRepository.deleteById(id);
        return true;
    }
}
