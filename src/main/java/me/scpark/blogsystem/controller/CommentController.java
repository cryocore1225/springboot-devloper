package me.scpark.blogsystem.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.scpark.blogsystem.domain.Comment;
import me.scpark.blogsystem.service.CommentService;

/**
 * 评论接口。
 *
 * <p>评论天然挂在文章下面，所以「新增 / 查询某篇文章的评论」这两条路径写成了
 * {@code /api/articles/{articleId}/comments}；而「查单条 / 删除」只需要评论 id，
 * 就单独放在 {@code /api/comments/{id}} 下。这也是 REST API 里常见的一种混搭写法。
 *
 * <p>这里没有用类级别的 @RequestMapping，因为同一个控制器要管两条不同的前缀。
 */
@RestController
public class CommentController {

    private final CommentService commentService;

    /**
     * @param commentService 评论业务逻辑层
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 查询某篇文章下的全部评论，按时间正序。
     *
     * @param articleId 文章编号
     * @return 200 和评论列表（可能是空列表）；文章不存在返回 404
     */
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<List<Comment>> findByArticle(@PathVariable Long articleId) {
        return commentService.findByArticleId(articleId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 给某篇文章新增一条评论。
     *
     * <p>请求体只需要 {@code {"author": "...", "content": "..."}}；
     * 文章 id 从 URL 里来，不用、也不能在请求体里传。
     *
     * @param articleId 文章编号
     * @param comment   请求体里的评论
     * @return 201 和新评论；文章不存在返回 404，作者或内容为空返回 400
     */
    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<Comment> create(@PathVariable Long articleId,
                                          @Valid @RequestBody Comment comment) {
        return commentService.create(articleId, comment)
                .map(created -> ResponseEntity
                        .created(URI.create("/api/comments/" + created.getId()))
                        .body(created))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 按 id 查询单条评论。
     *
     * @param id 评论编号
     * @return 200 和评论；查不到返回 404
     */
    @GetMapping("/api/comments/{id}")
    public ResponseEntity<Comment> findById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 删除一条评论。
     *
     * @param id 评论编号
     * @return 删除成功返回 204；该评论不存在返回 404
     */
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (commentService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
