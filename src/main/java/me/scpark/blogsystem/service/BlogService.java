package me.scpark.blogsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.dto.AddArticleRequest;
import me.scpark.blogsystem.repository.BlogRepository;

// 文章业务：存文章、查文章
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    // 新增文章
    public Article save(AddArticleRequest dto) {
        return blogRepository.save(dto.toEntity());
    }

    // 查询全部文章
    public List<Article> getAll() {
        return blogRepository.findAll();
    }
}
