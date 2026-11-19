package me.scpark.repository;

import me.scpark.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// 文章仓库：Spring Data JPA 自动实现增删改查
public interface BlogRepository extends JpaRepository<Article, Long> {
}
