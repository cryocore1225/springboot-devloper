# springboot-devloper

一个用于学习 **Spring Boot Java Web 开发** 的入门项目，包名 `me.scpark.springdeveloper`。

这个仓库按「**章节分支**」组织：每个分支是一个独立、可运行的学习阶段，越靠后的分支内容越完整。
这份 README 是**总入口**，放在默认分支 `master` 上；每一章的详细讲解（技术栈、代码说明、
接口文档、常见问题）在**各自分支的 README** 里。

## 章节导航

| 分支 | 本章内容 | 该分支的详细 README |
|------|----------|---------------------|
| [`chapter0`](https://github.com/cryocore1225/springboot-devloper/tree/chapter0) | 初始骨架：`build.gradle` + 静态首页 | 无 README |
| [`chapter1`](https://github.com/cryocore1225/springboot-devloper/tree/chapter1) | 加入 `TestController`，写出第一个 HTTP 接口 | 无 README |
| [`chapter2`](https://github.com/cryocore1225/springboot-devloper/tree/chapter2) | 与 `chapter1` 内容相同（同一提交） | 无 README |
| [`chapter3`](https://github.com/cryocore1225/springboot-devloper/tree/chapter3) | HTTP 映射入门：`@RequestMapping` / `@GetMapping`、静态首页、README 文档 | [README](https://github.com/cryocore1225/springboot-devloper/blob/chapter3/README.md) |
| [`chapter4`](https://github.com/cryocore1225/springboot-devloper/tree/chapter4) | 接入 H2 内存库 + Spring Data JPA，`/users` 真实 CRUD | [README](https://github.com/cryocore1225/springboot-devloper/blob/chapter4/README.md) |

各章一句话概括：

- **chapter0** — 只有 `build.gradle` 和 `src/main/resources/static/index.html`，还没有任何 Java 代码。
- **chapter1** — 出现第一个接口类 `TestController`，开始接收 HTTP 请求。
- **chapter3** — 补齐 `gradlew`、`settings.gradle`、`.gitignore` 和启动类 `BackendApplication`，
  项目第一次能完整跑起来，并配上 400 多行的 README。
- **chapter4** — 从「演示接口」推进到「真实数据操作」：按 `controller` / `service` / `repository` /
  `entity` 四层组织代码，`/users` 真正读写数据库。

## 怎么用这个仓库

```bash
git clone https://github.com/cryocore1225/springboot-devloper.git
cd springboot-devloper

git checkout chapter4     # 切到想看的那一章
```

然后读该分支的 `README.md`。

**最有价值的用法是比较两章之间的差异：**

```bash
git diff chapter3 chapter4
```

一眼就能看全这一章到底做了什么 —— 加了哪些依赖、新增了哪些类、改了哪些配置。
这是把仓库按章节分支组织的意义所在。

## 运行某一章

```bash
git checkout chapter4
./gradlew bootRun              # Windows PowerShell: .\gradlew.bat bootRun
```

启动后访问 <http://localhost:8080/>。

> **注意**：`chapter0` ~ `chapter2` 既没有 `gradlew` 也没有 `settings.gradle`，**跑不起来**，
> 只用于对照阅读代码。从 `chapter3` 起才能构建运行。

## 环境要求

- JDK 22
- Gradle Wrapper（`chapter3` 起仓库自带，无需单独安装 Gradle）

## 关于分支组织的两点说明

- `chapter1` 和 `chapter2` 目前指向同一个提交，内容完全一致。
- `master`（本分支）与 `chapter0` 的内容相同，保留它作为仓库的默认分支和这份总入口。

章节内容只在各自的分支里维护，这里不重复。
