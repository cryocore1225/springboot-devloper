package me.scpark.blogsystem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import me.scpark.blogsystem.domain.Article;

/**
 * ArticleRepository 的测试。
 *
 * <p>{@code @DataJpaTest} 只启动数据源、Hibernate 和 Repository，不加载 Controller 和 Service，
 * 比 {@code @SpringBootTest} 快，而且每个测试方法跑完会自动回滚。
 *
 * <p>这里重点验证三种查询：分页、发布状态过滤、标题搜索。
 * data.sql 里预置了 4 篇文章，其中 3 篇已发布、1 篇草稿。
 */
@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("分页查询：按发布时间倒序，每页条数生效")
    void findsFirstPage() {
        Page<Article> page = articleRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(2);
        // 最新的那篇是 2026-11-01 创建的草稿，所以排第一。
        assertThat(page.getContent().get(0).getTitle()).contains("草稿");
    }

    @Test
    @DisplayName("按发布状态过滤：已发布列表里没有草稿")
    void filtersByPublished() {
        Page<Article> published = articleRepository
                .findByPublishedOrderByCreatedAtDesc(true, PageRequest.of(0, 10));

        assertThat(published.getTotalElements()).isEqualTo(3);
        assertThat(published.getContent()).allMatch(Article::isPublished);
    }

    @Test
    @DisplayName("按标题模糊搜索：忽略大小写")
    void searchesByTitleIgnoreCase() {
        Page<Article> result = articleRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc("jpa", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).contains("JPA");
    }

    @Test
    @DisplayName("新增文章：创建时间和更新时间会自动填好")
    void savesWithTimestamps() {
        Article saved = articleRepository.save(new Article("新文章", "正文内容", "张三"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
