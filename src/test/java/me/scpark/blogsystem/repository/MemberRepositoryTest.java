package me.scpark.blogsystem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import me.scpark.blogsystem.domain.Member;

/**
 * MemberRepository 的测试。
 *
 * <p>{@code @DataJpaTest} 只启动数据源、Hibernate 和 Repository，不加载 Controller 和 Service，
 * 比 {@code @SpringBootTest} 快，而且每个测试方法跑完会自动回滚。
 */
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 查询全部成员。
     *
     * <p>这个方法没加 {@code @Sql}，查的是 {@code data.sql} 预置的三条数据 ——
     * 也就是应用启动时本来就有的那些。
     */
    @Test
    @DisplayName("getAllMembers：查出全部成员")
    public void getAllMembers() {
        List<Member> members = memberRepository.findAll();

        assertThat(members).hasSize(3);
    }

    /**
     * 按 id 查询成员。
     *
     * <p>{@code @Sql} 会在测试方法执行之前先跑指定的 SQL 脚本，
     * 把这条测试要用的数据准备好。脚本放在 {@code src/test/resources} 下，
     * 只在跑测试时才加载，不会影响应用本身。
     */
    @Test
    @DisplayName("getMemberById：按 id 能查到成员")
    @Sql("/insert-members.sql")
    public void getMemberById() {
        Optional<Member> result = memberRepository.findById(100L);
        Member member = result.get();

        assertThat(member.getName()).isEqualTo("唐僧");
    }

    /**
     * 按姓名查询成员。
     *
     * <p>换个数据脚本，查的是另一条数据。
     */
    @Test
    @DisplayName("getMemberByName：按姓名能查到成员")
    @Sql("/member-data.sql")
    public void getMemberByName() {
        Optional<Member> result = memberRepository.findByName("孙悟空");
        Member member = result.get();

        assertThat(member.getEmail()).isEqualTo("sunwukong@example.com");
    }

    /**
     * 新增成员。
     *
     * <p>这个对象是用带参构造函数新建的，id 还是 null，
     * 所以 save 走的是 insert，保存完 id 会被数据库填上。
     */
    @Test
    @DisplayName("saveMember：新增成员，能拿到自增 id")
    public void saveMember() {
        Member saved = memberRepository.save(new Member("赵六", "zhaoliu@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(memberRepository.findById(saved.getId())).isPresent();
    }

    /**
     * 按 id 删除成员。
     *
     * <p>借着同一个脚本塞一条进来，再把它删掉 —— 删除本来就需要一条已存在的数据。
     */
    @Test
    @DisplayName("deleteMember：按 id 能删除成员")
    @Sql("/insert-members.sql")
    public void deleteMember() {
        memberRepository.deleteById(100L);

        assertThat(memberRepository.findById(100L)).isEmpty();
    }

    @Test
    @DisplayName("DeleteAll")
    @Sql("/insert-members.sql")
    public void deleteAll() {
        memberRepository.deleteAll();

        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(0);
    }
}
