package me.scpark.blogsystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import me.scpark.blogsystem.domain.Article;
import me.scpark.blogsystem.service.BlogService;

// 页面控制器：文章列表页和详情页
@Controller
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogService blogService;

    // 文章列表页
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<Article> articles = blogService.getAll();
        model.addAttribute("articles", articles);
        return "articles";
    }

    // 文章详情页：id 不存在就回列表
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Optional<Article> article = blogService.getOne(id);
        if (article.isEmpty()) {
            return "redirect:/articles";
        }
        model.addAttribute("article", article.get());
        return "article";
    }
}
