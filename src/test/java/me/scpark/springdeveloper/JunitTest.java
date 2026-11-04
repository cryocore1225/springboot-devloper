package me.scpark.springdeveloper;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.scpark.springdeveloper.repository.MemberRepository;
import me.scpark.springdeveloper.service.MemberService;

/**
 * 最基础的 Spring Boot 测试：验证整个应用容器能不能正常启动。
 *
 * <p>类上的 {@code @SpringBootTest} 是它和普通单元测试最大的区别：
 * 它会真的把整个 Spring 容器跑起来 —— 扫描组件、创建连接池、连 H2、让 Hibernate 建表、
 * 执行 {@code data.sql}，跟你按「运行」按钮启动项目时做的事情一模一样。
 *
 * <p>之所以说它是最有用的第一个测试，是因为上面这一长串步骤里**任何一步出问题**，
 * 测试都会直接失败：
 * <ul>
 *   <li>配置写错了（比如 {@code defer-datasource-initialization} 漏了）</li>
 *   <li>Bean 装配冲突、循环依赖</li>
 *   <li>实体类和表结构对不上</li>
 *   <li>依赖少了、驱动找不到</li>
 * </ul>
 * 换成手动验证，你得启动项目、开浏览器、一个个接口点过去才能发现；
 * 这里只要跑一条命令，几秒钟就知道结果。
 *
 * <p>注意：默认用的是 MOCK 环境，**不会真的占用 8080 端口**，
 * 所以项目正在运行时也能跑这个测试，不会冲突。
 */
@SpringBootTest
class JunitTest {

    // 测试类里用字段注入是常见做法，比构造函数注入写起来短，
    // 而且测试类不会被别的类依赖，不存在「依赖藏起来」的顾虑。
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Spring 容器能正常启动")
    void contextLoads() {
        // 方法体是空的，这正是这个测试的重点。
        // 它不检查任何东西，只要求「跑完不抛异常」——
        // 而能跑完，就等于上面那串启动流程全部成功了。
        // 这是 Spring Initializr 生成项目时自带的那一个测试，几乎是所有项目的起点。
    }

    @Test
    @DisplayName("容器里能拿到各个 Bean：组件扫描和自动配置都生效了")
    void beansAreWired() {
        // 如果 MemberService 没被扫描到、Repository 没被生成实现类，
        // 注入阶段容器就起不来了，或者这里会是 null。
        assertThat(memberService).isNotNull();
        assertThat(memberRepository).isNotNull();
    }

    @Test
    @DisplayName("数据源能连上 H2 内存库")
    void dataSourceConnectsToH2() throws Exception {
        // try-with-resources：连接用完自动关闭，不会泄漏。
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(1)).isTrue();
            // 顺带把连的到底是什么库也断言一下，免得哪天配置改错了却没人发现。
            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("H2");
        }
    }

    @Test
    @DisplayName("data.sql 预置的数据确实进了库")
    void dataSqlIsLoaded() {
        // 这一个测试专门守住 application.properties 里那两行配置：
        //   spring.jpa.defer-datasource-initialization=true
        //   spring.sql.init.encoding=UTF-8
        // 少了前者，data.sql 会在建表之前执行，启动时直接报「表不存在」；
        // 编码配错，中文名会变成乱码。
        // 两种情况都会让这条断言失败。
        assertThat(memberRepository.findByEmail("zhangsan@example.com"))
                .isPresent()
                .get()
                .extracting("name")
                .isEqualTo("张三");
    }
}
