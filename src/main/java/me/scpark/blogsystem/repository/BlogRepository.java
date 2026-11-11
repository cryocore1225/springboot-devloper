package me.scpark.blogsystem.repository;

import me.scpark.blogsystem.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// 文章仓库：Spring Data JPA 自动实现增删改查
public interface BlogRepository extends JpaRepository<Article, Long> {
}
