package me.scpark.blogsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.scpark.blogsystem.domain.Article;

// 新增文章时前端传来的数据
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddArticleRequest {

    private String title;
    private String content;

    // 转成 Article
    public Article toEntity() {
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
