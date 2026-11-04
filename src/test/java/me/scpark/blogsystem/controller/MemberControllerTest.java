package me.scpark.blogsystem.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import me.scpark.blogsystem.domain.Member;
import me.scpark.blogsystem.service.MemberService;

/**
 * MemberController 的测试。
 *
 * <p>这是最基础的一种写法：不启动 Spring，就当普通 Java 类来测。
 * 用 mock() 造一个假的 MemberService 塞进控制器，直接调方法、看返回值。
 */
class MemberControllerTest {

    @Test
    @DisplayName("findAll 返回 200 和成员列表")
    void findAll() {
        // 1. 造一个假的 Service，规定它的 findAll() 返回什么
        MemberService service = mock(MemberService.class);
        Member member = new Member("张三", "zhangsan@example.com");
        member.setId(1L);
        when(service.findAll()).thenReturn(List.of(member));

        // 2. 把假 Service 交给控制器
        MemberController controller = new MemberController(service);

        // 3. 直接调方法，检查返回值
        ResponseEntity<List<Member>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("张三");
    }
}
