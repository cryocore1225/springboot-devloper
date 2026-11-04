package me.scpark.blogsystem.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.dto.PageResult;
import me.scpark.blogsystem.service.ArticleService;

/**
 * 文章接口，博客系统最核心的一组 API。
 *
 * <p>类上的 @RequestMapping("/api/articles") 是统一前缀，
 * 下面每个方法只写自己那一段路径，拼起来就是完整地址。
 *
 * <p>分页参数用 query string 传：{@code ?page=0&size=10}，
 * 页码从 0 开始 —— 这是 Spring Data 的约定，和前端常见的「第 1 页」差一位，写前端时要注意。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * @param articleService 文章业务逻辑层
     */
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 分页查询文章列表，最新的排在最前面。
     *
     * @param page      页码，从 0 开始，默认 0
     * @param size      每页条数，默认 10，最大 50
     * @param published 可选：true 只看已发布，false 只看草稿，不传则全部
     * @return 200 和一页文章
     */
    @GetMapping
    public ResponseEntity<PageResult<Article>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean published) {
        return ResponseEntity.ok(articleService.findPage(page, size, published));
    }

    /**
     * 按标题关键字搜索文章（模糊匹配、忽略大小写）。
     *
     * @param keyword 标题里包含的关键字
     * @param page    页码，默认 0
     * @param size    每页条数，默认 10
     * @return 200 和一页匹配的文章
     */
    @GetMapping("/search")
    public ResponseEntity<PageResult<Article>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.searchByTitle(keyword, page, size));
    }

    /**
     * 按 id 查询单篇文章。
     *
     * @param id 文章编号，来自 URL 路径
     * @return 查到返回 200 和文章；查不到返回 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> findById(@PathVariable Long id) {
        return articleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 新增文章。
     *
     * <p>参数上的 {@code @Valid} 会触发实体字段上的校验注解：
     * 标题、正文、作者为空都会直接返回 400，不会走到数据库。
     *
     * @param article 请求体里的 JSON，@RequestBody 会自动把它转换成 Article 对象
     * @return 201 和新创建的文章（带上数据库生成的自增 id）
     */
    @PostMapping
    public ResponseEntity<Article> create(@Valid @RequestBody Article article) {
        Article created = articleService.create(article);
        // 201 表示"已创建"；Location 响应头告诉调用方这个新资源的地址。
        return ResponseEntity.created(URI.create("/api/articles/" + created.getId())).body(created);
    }

    /**
     * 整体替换一篇文章（PUT）。
     * 请求体里必须提供全部字段，没提供的会被置为 null（published 不传则变成 false）。
     *
     * @return 200 和修改后的文章；该 id 不存在返回 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                          @Valid @RequestBody Article article) {
        return articleService.update(id, article)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 局部更新一篇文章（PATCH）。
     * 只有请求体里出现的字段才会被修改，其他字段保持原样。
     *
     * @return 200 和修改后的文章；该 id 不存在返回 404
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Article> patch(@PathVariable Long id, @RequestBody Article article) {
        return articleService.patch(id, article)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 把草稿发布出去。
     *
     * <p>单独开一个动作接口，是因为 PATCH 里 publish 字段是基本类型 boolean，
     * 没法区分「没传」和「传了 false」；单独开接口语义也更清楚。
     *
     * @return 200 和发布后的文章；该 id 不存在返回 404
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<Article> publish(@PathVariable Long id) {
        return articleService.setPublished(id, true)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 把已发布的文章撤回成草稿。
     *
     * @return 200 和撤回后的文章；该 id 不存在返回 404
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<Article> unpublish(@PathVariable Long id) {
        return articleService.setPublished(id, false)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 删除文章。
     *
     * <p>文章下面的评论会一起删掉，不会有孤儿评论留在库里。
     *
     * @return 删除成功返回 204（表示成功但没有响应体）；该文章不存在返回 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (articleService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
