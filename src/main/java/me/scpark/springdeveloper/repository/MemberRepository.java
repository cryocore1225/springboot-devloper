package me.scpark.springdeveloper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.scpark.springdeveloper.domain.Member;

/**
 * 成员的数据访问层，负责和数据库打交道。
 *
 * <p>这里只需要声明一个接口，不用写实现类：只要继承 JpaRepository 并填好泛型，
 * Spring Data JPA 会在启动时自动生成实现，
 * findAll、findById、save、deleteById、existsById 等方法就都能直接用了。
 *
 * <p>两个泛型参数分别是：要操作的实体类（Member）和它的主键类型（Long）。
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 按邮箱查找成员。
     *
     * <p>方法名本身就是一条查询语句：findBy 后面跟实体类的字段名 Email，
     * Spring Data JPA 会自动把它翻译成 {@code where email = ?}，这叫派生查询。
     *
     * @param email 成员邮箱
     * @return 查到的成员；没有对应成员时返回空 Optional
     */
    Optional<Member> findByEmail(String email);

    /**
     * 按姓名查找成员。
     *
     * <p>同样是一条派生查询，会被翻译成 {@code where name = ?}。
     *
     * @param name 成员姓名
     * @return 查到的成员；没有对应成员时返回空 Optional
     */
    Optional<Member> findByName(String name);
}
