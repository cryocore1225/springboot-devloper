package me.scpark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.scpark.domain.Article;
import me.scpark.dto.AddArticleRequest;
import me.scpark.dto.UpdateArticleRequest;
import me.scpark.repository.BlogRepository;

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
    // @Transactional 让整个方法在一个事务里跑：查出来的 article 是"托管"状态，
    // 改完不用再 save，事务提交时 Hibernate 会自己发 update。
    @Transactional
    public Optional<Article> update(Long id, UpdateArticleRequest dto) {
        return blogRepository.findById(id)
                .map(article -> {
                    article.update(dto.getTitle(), dto.getContent());
                    return article;
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
