package me.scpark.blogsystem.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.dto.AddArticleRequest;
import me.scpark.blogsystem.repository.BlogRepository;

// 测试 BlogApiController 的 5 个接口
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlogRepository blogRepository;

    // 先往库里存一条，给 GET / PUT / DELETE 的测试用
    private Article saveArticle(String title, String content) {
        return blogRepository.save(new Article(title, content));
    }

    @Test
    @DisplayName("POST /api/articles：新增文章返回 201")
    void addArticle() throws Exception {
        AddArticleRequest request = new AddArticleRequest("第一篇", "第一篇的正文");

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("第一篇"))
                .andExpect(jsonPath("$.content").value("第一篇的正文"));
    }

    @Test
    @DisplayName("GET /api/articles：查询全部文章返回 200 和数组")
    void getAllArticles() throws Exception {
        saveArticle("第一篇", "正文1");
        saveArticle("第二篇", "正文2");

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    @DisplayName("GET /api/articles/{id}：查到返回 200，查不到返回 404")
    void getArticle() throws Exception {
        Article article = saveArticle("第二篇", "正文2");

        mockMvc.perform(get("/api/articles/" + article.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("第二篇"));

        mockMvc.perform(get("/api/articles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/articles/{id}：修改成功返回 200，id 不存在返回 404")
    void updateArticle() throws Exception {
        Article article = saveArticle("原标题", "原正文");
        AddArticleRequest request = new AddArticleRequest("改后标题", "改后正文");

        mockMvc.perform(put("/api/articles/" + article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("改后标题"))
                .andExpect(jsonPath("$.content").value("改后正文"));

        mockMvc.perform(put("/api/articles/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/articles/{id}：删除返回 204，再删返回 404")
    void deleteArticle() throws Exception {
        Article article = saveArticle("待删除", "正文");

        mockMvc.perform(delete("/api/articles/" + article.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/articles/" + article.getId()))
                .andExpect(status().isNotFound());
    }
}
