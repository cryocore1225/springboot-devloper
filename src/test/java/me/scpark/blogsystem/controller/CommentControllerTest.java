package me.scpark.blogsystem.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

/**
 * CommentController 的接口测试。
 *
 * <p>重点看两件事：
 * <ul>
 *   <li>「文章不存在」要返回 404，而不是 500 —— 这靠 Service 层先查文章是否存在来保证；</li>
 *   <li>评论和文章是两条不同的资源，创建走 {@code /api/articles/{id}/comments}，
 *       删除走 {@code /api/comments/{id}}。</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/articles/1/comments 返回该文章的评论，按时间正序")
    void findByArticle() throws Exception {
        mockMvc.perform(get("/api/articles/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("李四"))
                .andExpect(jsonPath("$[0].article").doesNotExist());
    }

    @Test
    @DisplayName("GET 不存在的文章的评论返回 404")
    void findByMissingArticle() throws Exception {
        mockMvc.perform(get("/api/articles/999/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/articles/1/comments 发评论返回 201")
    void create() throws Exception {
        mockMvc.perform(post("/api/articles/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"author":"新读者","content":"写得清楚，收藏了。"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/comments/")))
                .andExpect(jsonPath("$.author").value("新读者"));
    }

    @Test
    @DisplayName("给不存在的文章发评论返回 404")
    void createForMissingArticle() throws Exception {
        mockMvc.perform(post("/api/articles/999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"author":"新读者","content":"这条评论不该被存进去"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("评论内容为空返回 400")
    void createWithBlankContent() throws Exception {
        mockMvc.perform(post("/api/articles/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"author":"新读者","content":"  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").value("评论内容不能为空"));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} 删除评论返回 204")
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
    }
}
