package me.scpark.blogsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.scpark.blogsystem.domain.Comment;

/**
 * 评论的数据访问层。
 *
 * <p>方法名里的下划线 {@code Article_Id} 不是随便写的：Comment 里存关联的是
 * {@code Article article} 这个对象，要按它的主键查就得写成 {@code Article_Id}，
 * 属性路径用下划线隔开，Spring Data JPA 才知道要翻译成 {@code where article_id = ?}。
 * 写成 {@code findByArticleId} 会被当成 Comment 上有个叫 articleId 的字段，启动时直接报错。
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 查询某篇文章下的全部评论，按时间正序（最早的排前面，符合论坛/评论区的阅读习惯）。
     *
     * @param articleId 文章编号
     * @return 该文章的评论列表；一条都没有时返回空列表
     */
    List<Comment> findByArticle_IdOrderByCreatedAtAsc(Long articleId);

    /**
     * 删除某篇文章下的全部评论，返回实际删掉的条数。
     *
     * <p>派生删除查询：方法名以 {@code deleteBy} 开头即可，不用自己写 SQL。
     * 它必须在事务里执行，所以调用方 {@code ArticleService.delete} 上带了 {@code @Transactional}。
     *
     * @param articleId 文章编号
     * @return 被删除的评论条数
     */
    long deleteByArticle_Id(Long articleId);

    /**
     * 统计某篇文章下的评论数，博客列表里显示「N 条评论」时用得到。
     *
     * @param articleId 文章编号
     * @return 评论条数
     */
    long countByArticle_Id(Long articleId);
}
