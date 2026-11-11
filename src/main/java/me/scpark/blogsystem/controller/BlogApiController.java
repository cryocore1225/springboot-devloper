package me.scpark.blogsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.dto.AddArticleRequest;
import me.scpark.blogsystem.dto.ArticleResponse;
import me.scpark.blogsystem.service.BlogService;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    // 新增文章
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    // 查询全部文章
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        List<ArticleResponse> articleResponselist = blogService.getAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articleResponselist);
    }
}
