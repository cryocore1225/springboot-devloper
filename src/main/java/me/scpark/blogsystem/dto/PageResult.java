package me.scpark.blogsystem.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * 分页结果，专供接口返回给前端使用。
 *
 * <p>为什么不直接把 Spring Data 的 {@link Page} 返回出去？
 * 一是 {@code Page} 的实现类 {@code PageImpl} 序列化出的 JSON 结构又长又杂，
 * 前端要用的其实只有「本页数据 + 页码 + 总数」这几项；
 * 二是 Spring Boot 3.3 之后对直接序列化 {@code PageImpl} 会打警告。
 * 所以这里用一个 record 把它压成前端好用的形状。
 *
 * <p>record 是 Java 16 引入的写法，编译后自动生成构造函数、getter 和 equals 等方法，
 * 适合这种「只用来装数据、没有行为」的类。
 *
 * @param content       本页数据
 * @param page          当前页码，从 0 开始
 * @param size          每页条数
 * @param totalElements 总条数
 * @param totalPages    总页数
 * @param last          是否已是最后一页（前端「加载更多」按钮靠它判断要不要隐藏）
 * @param <T>           本页数据的类型
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    /**
     * 把一个 Spring Data 的 Page 转成接口返回用的 PageResult。
     *
     * @param page 从数据库查出来的分页结果
     * @param <T>  数据类型
     * @return 精简后的分页结果
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
