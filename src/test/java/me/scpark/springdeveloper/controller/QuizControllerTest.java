package me.scpark.springdeveloper.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /quiz 参数 code=2 返回 400")
    void quiz() throws Exception {
        mockMvc.perform(get("/quiz").param("code", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Bad Request")));
    }

    @Test
    @DisplayName("GET /quiz 参数 code=100 返回 200")
    void getQuiz() throws Exception {
        mockMvc.perform(get("/quiz").param("code", "100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /quiz 参数 code=2 返回 200")
    void postQuiz2() throws Exception {
        mockMvc.perform(post("/quiz").param("code", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Success")));
    }

    @Test
    @DisplayName("POST /quiz 参数 code=1 返回 403")
    void postQuiz1() throws Exception {
        mockMvc.perform(post("/quiz").param("code", "1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Forbidden")));
    }
}
