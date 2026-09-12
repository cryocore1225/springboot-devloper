package me.scpark.springdeveloper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.scpark.springdeveloper.entity.User;

/**
 * 用户的数据访问层，负责和数据库打交道。
 *
 * <p>这里只需要声明一个接口，不用写实现类：只要继承 JpaRepository 并填好泛型，
 * Spring Data JPA 会在启动时自动生成实现，
 * findAll、findById、save、deleteById、existsById 等方法就都能直接用了。
 *
 * <p>两个泛型参数分别是：要操作的实体类（User）和它的主键类型（Long）。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按邮箱查找用户。
     *
     * <p>方法名本身就是一条查询语句：findBy 后面跟实体类的字段名 Email，
     * Spring Data JPA 会自动把它翻译成 {@code where email = ?}，这叫派生查询。
     *
     * @param email 用户邮箱
     * @return 查到的用户；没有对应用户时返回空 Optional
     */
    Optional<User> findByEmail(String email);
}
