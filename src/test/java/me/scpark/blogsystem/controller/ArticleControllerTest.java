package me.scpark.blogsystem.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
 * ArticleController 的接口测试。
 *
 * <p>和 {@link MemberControllerTest} 那种「当普通 Java 类直接调方法」的写法不同，
 * 这里用 MockMvc 真正走一遍 Spring MVC：路由、JSON 序列化、参数校验、状态码全部覆盖。
 *
 * <p>类上加了 {@code @Transactional}：每个测试方法跑完自动回滚，
 * 所以「删文章」这种测试不会影响其他测试，也不会污染 data.sql 的初始数据。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/articles 默认返回第一页 10 条")
    void findAll() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    @DisplayName("GET /api/articles?size=2 每页只返回 2 条")
    void findAllWithPaging() throws Exception {
        mockMvc.perform(get("/api/articles").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("GET /api/articles?published=false 只返回草稿")
    void findDraftsOnly() throws Exception {
        mockMvc.perform(get("/api/articles").param("published", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].published").value(false));
    }

    @Test
    @DisplayName("GET /api/articles/search?keyword=jpa 忽略大小写匹配标题")
    void search() throws Exception {
        mockMvc.perform(get("/api/articles/search").param("keyword", "jpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title", containsString("JPA")));
    }

    @Test
    @DisplayName("GET /api/articles/1 返回文章详情")
    void findById() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("张三"));
    }

    @Test
    @DisplayName("GET /api/articles/999 返回 404")
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/articles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/articles 创建成功返回 201 和 Location")
    void create() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"测试新建的文章","content":"正文","author":"测试员","tags":"测试"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/articles/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    @DisplayName("POST /api/articles 标题为空返回 400，并指出是哪个字段")
    void createWithBlankTitle() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"   ","content":"正文","author":"测试员"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("标题不能为空"));
    }

    @Test
    @DisplayName("PATCH /api/articles/{id} 只改传了的字段")
    void patchTitle() throws Exception {
        mockMvc.perform(patch("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"改过的标题"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("改过的标题"))
                .andExpect(jsonPath("$.content", containsString("练习 HTTP 映射")));
    }

    @Test
    @DisplayName("POST /api/articles/{id}/publish 把草稿发布出去")
    void publish() throws Exception {
        mockMvc.perform(post("/api/articles/4/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    @DisplayName("DELETE /api/articles/{id} 删除后返回 204，再查就是 404")
    void deleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/articles/3"))
                .andExpect(status().isNotFound());
    }
}
