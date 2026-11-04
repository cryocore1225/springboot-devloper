package me.scpark.blogsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域（CORS）配置。
 *
 * <p>浏览器有一条同源策略：前端跑在 {@code http://localhost:3000}、
 * 后端跑在 {@code http://localhost:8080}，端口不同也算「跨域」，
 * 默认情况下浏览器会拦截前端发起的请求。开发时最常见的解决办法就是让后端明确告诉浏览器：
 * 「这些来源的请求我允许」。
 *
 * <p>这里为了本地开发方便放开了 {@code /api/**} 下的所有来源。上线前要改成真实域名，
 * 否则等于把接口对所有网站开放。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600); // 预检请求的结果缓存一小时，省掉重复的 OPTIONS 往返。
    }
}
