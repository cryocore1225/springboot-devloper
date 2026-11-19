package me.scpark.blogsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import me.scpark.blogsystem.service.BlogService;

// 页面控制器：返回模板名，由 Thymeleaf 渲染 templates/ 下的 html
@Controller
@RequiredArgsConstructor
public class ExampleController {

    private final BlogService blogService;

    // "/" 和 "/thymeleaf/Example" 都打开同一个页面
    @GetMapping({"/", "/thymeleaf/Example"})
    public String thymeleaf(Model model) {
        model.addAttribute("title", "Blogsystem backend");
        model.addAttribute("articles", blogService.getAll());
        return "Example";
    }
}
