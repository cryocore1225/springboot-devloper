package me.scpark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 修改文章时前端传来的数据
@Getter
@AllArgsConstructor
public class UpdateArticleRequest {

    private String title;
    private String content;
}
