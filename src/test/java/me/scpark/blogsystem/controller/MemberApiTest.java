package me.scpark.blogsystem.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * 成员接口的接口测试，重点验证统一异常处理（{@link ApiExceptionHandler}）。
 *
 * <p>成员接口本身和原来一样是 CRUD，但这里多测了两条边界：
 * 邮箱格式不合法返回 400；邮箱和已有成员重复返回 409（不是 500）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/members 返回预置的 3 个成员")
    void findAll() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("邮箱格式不对返回 400，并指出是 email 字段")
    void createWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"赵六","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("邮箱格式不正确"));
    }

    @Test
    @DisplayName("邮箱重复返回 409")
    void createWithDuplicateEmail() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"另一个张三","email":"zhangsan@example.com"}
                                """))
                .andExpect(status().isConflict());
    }
}
