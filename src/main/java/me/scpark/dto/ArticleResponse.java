package me.scpark.dto;

import lombok.Getter;
import me.scpark.domain.Article;

// 返回给前端的文章数据（id、标题、正文）
@Getter
public class ArticleResponse {

    private final Long id;
    private final String title;
    private final String content;

    // 把 Article 转成 ArticleResponse
    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
