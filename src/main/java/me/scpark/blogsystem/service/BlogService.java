package me.scpark.blogsystem.service;

import java.util.List;
import java.util.Optional;

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

    // 查询一篇文章：有就返回文章，没有返回空 Optional
    public Optional<Article> getOne(Long id) {
        return blogRepository.findById(id);
    }

    // 修改文章：id 不存在返回空 Optional
    public Optional<Article> update(Long id, AddArticleRequest dto) {
        return blogRepository.findById(id)
                .map(article -> {
                    article.update(dto.getTitle(), dto.getContent());
                    return blogRepository.save(article);
                });
    }

    // 删除文章：删成功返回 true，id 不存在返回 false
    public boolean delete(Long id) {
        if (!blogRepository.existsById(id)) {
            return false;
        }
        blogRepository.deleteById(id);
        return true;
    }
}
