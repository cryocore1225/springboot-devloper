package me.scpark.springdeveloper.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.scpark.springdeveloper.domain.Member;
import me.scpark.springdeveloper.repository.MemberRepository;

/**
 * 成员的业务逻辑层，增删改查的具体处理都放在这里。
 *
 * <p>控制器只负责收发 HTTP 请求，真正干什么由这一层决定。
 *
 * <p>类上的 @Transactional(readOnly = true) 表示下面的方法默认运行在只读事务里；
 * 真正要写数据的方法会各自覆盖成可写事务。
 */
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 通过构造函数注入 Repository。
     *
     * <p>只有一个构造函数时，Spring 会自动用它完成注入，不需要再写 @Autowired。
     * 字段声明成 final，可以保证依赖一旦注入就不会被改掉。
     *
     * @param memberRepository 成员数据访问层
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 查询全部成员。
     *
     * @return 成员列表；一条都没有时返回空列表
     */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * 按 id 查询成员。
     *
     * @param id 成员编号
     * @return 查到的成员；不存在时返回空 Optional
     */
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * 新增成员。
     *
     * @param member 要新增的成员
     * @return 新增后的成员，此时已经带上数据库生成的自增 id
     */
    @Transactional
    public Member create(Member member) {
        // save 会根据主键是否为空自行判断是新增还是更新，
        // 这里的 member 主键为空，所以走的是新增，对应一条 insert 语句。
        return memberRepository.save(member);
    }

    /**
     * 整体替换一个成员的信息，对应 HTTP 的 PUT。
     *
     * <p>请求里没提供的字段会被一并覆盖成 null。
     *
     * @param id     要修改的成员编号
     * @param source 新的成员信息
     * @return 修改后的成员；该 id 不存在时返回空 Optional
     */
    @Transactional
    public Optional<Member> update(Long id, Member source) {
        return memberRepository.findById(id).map(member -> {
            member.setName(source.getName());
            member.setEmail(source.getEmail());
            return member;
        });
        // 这里没有调用 save：从数据库查出来的 member 已经处于 Hibernate 的"托管"状态，
        // 方法结束、事务提交时，Hibernate 会自动比对字段有没有变，变了就生成 update 语句。
    }

    /**
     * 局部更新成员信息，对应 HTTP 的 PATCH，只覆盖请求里真正提供了的字段。
     *
     * @param id     要修改的成员编号
     * @param source 只填了想改的字段的成员对象
     * @return 修改后的成员；该 id 不存在时返回空 Optional
     */
    @Transactional
    public Optional<Member> patch(Long id, Member source) {
        return memberRepository.findById(id).map(member -> {
            if (source.getName() != null) {
                member.setName(source.getName());
            }
            if (source.getEmail() != null) {
                member.setEmail(source.getEmail());
            }
            return member;
        });
    }

    /**
     * 按 id 删除成员。
     *
     * @param id 要删除的成员编号
     * @return 删除成功返回 true；该 id 本来就不存在则返回 false
     */
    @Transactional
    public boolean delete(Long id) {
        if (!memberRepository.existsById(id)) {
            return false;
        }
        memberRepository.deleteById(id);
        return true;
    }
}
