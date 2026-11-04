# Blogsystem backend

博客系统后端：用 **Spring Boot 3.4 + Spring Data JPA + H2** 实现的一套文章 / 评论 / 成员接口。
给博客前端（例如本工作区里的 NotionNext 站点或 blog-manager 管理面板）提供数据接口，
也可以直接当 Spring Boot 分层与 JPA 的练习项目看。

项目原名 `springboot-devloper`，2026-11-04 起改造为博客系统后端：项目名、包名、启动类、
表结构和接口全部换成了博客业务，原来 chapter0～chapter4 的学习阶段代码仍保留在 Git 历史里。

## 目录

- [项目简介](#项目简介)
- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [数据模型](#数据模型)
- [一次请求走完四层](#一次请求走完四层)
- [数据库配置](#数据库配置)
- [运行项目](#运行项目)
- [接口用法示例](#接口用法示例)
- [测试](#测试)
- [从学习项目改造过来做了什么](#从学习项目改造过来做了什么)
- [常见问题](#常见问题)
- [后续计划](#后续计划)

## 项目简介

- 项目名：`blogsystem-backend`
- 包名：`me.scpark.blogsystem`
- 启动类：`me.scpark.blogsystem.BlogsystemApplication`
- 默认端口：`8080`
- 数据库：H2 内存库 `jdbc:h2:mem:blogdb`（启动时建表并写入 `data.sql` 的示例数据）

当前实现的能力：

1. 文章 CRUD，支持分页、按发布状态过滤、按标题模糊搜索。
2. 草稿与发布两个状态，可发布、可撤回。
3. 评论挂在文章下面，发评论、查评论、删评论；删文章会连带删评论。
4. 站内成员 CRUD，邮箱唯一。
5. 参数校验（标题/正文/作者不能为空、邮箱格式、长度上限），错误统一返回 400 + 字段名。
6. 数据库约束冲突（例如邮箱重复）返回 409，而不是 500。
7. 开发期跨域配置，前端可以直接从别的端口访问 `/api/**`。

## 功能概览

### 文章

| 功能 | 方法与地址 | 说明 |
|---|---|---|
| 分页列表 | `GET /api/articles?page=0&size=10` | 按发布时间倒序；`page` 从 **0** 开始，`size` 最大 50 |
| 只看已发布 | `GET /api/articles?published=true` | 加 `published=false` 则只看草稿 |
| 标题搜索 | `GET /api/articles/search?keyword=JPA` | 模糊匹配、忽略大小写，同样支持分页 |
| 单篇详情 | `GET /api/articles/{id}` | 查不到返回 404 |
| 新建 | `POST /api/articles` | 成功 201 + `Location` 响应头 |
| 整体替换 | `PUT /api/articles/{id}` | 必须传全部字段，`published` 不传会变成 `false` |
| 局部更新 | `PATCH /api/articles/{id}` | 只改请求体里出现的字段（`published` 除外） |
| 发布 | `POST /api/articles/{id}/publish` | 草稿 → 已发布 |
| 撤回 | `POST /api/articles/{id}/unpublish` | 已发布 → 草稿 |
| 删除 | `DELETE /api/articles/{id}` | 成功 204，该文章的评论一起删 |

### 评论

| 功能 | 方法与地址 | 说明 |
|---|---|---|
| 某文章的评论 | `GET /api/articles/{articleId}/comments` | 按时间正序；文章不存在返回 404 |
| 发评论 | `POST /api/articles/{articleId}/comments` | 请求体只需 `author`、`content`，成功 201 |
| 单条评论 | `GET /api/comments/{id}` | 查不到返回 404 |
| 删除评论 | `DELETE /api/comments/{id}` | 成功 204 |

### 成员

| 功能 | 方法与地址 | 说明 |
|---|---|---|
| 全部成员 | `GET /api/members` | — |
| 单个成员 | `GET /api/members/{id}` | 查不到返回 404 |
| 新增 | `POST /api/members` | 邮箱重复返回 409 |
| 整体替换 | `PUT /api/members/{id}` | — |
| 局部更新 | `PATCH /api/members/{id}` | — |
| 删除 | `DELETE /api/members/{id}` | 成功 204 |

### 其他

| 功能 | 地址 | 说明 |
|---|---|---|
| 接口索引页 | `GET /` | 静态 HTML，列出全部接口并可直接点开 GET 接口 |
| 数据库控制台 | `GET /h2-console` | JDBC URL 填 `jdbc:h2:mem:blogdb`，用户名 `sa`，密码留空 |

状态码约定：`200` 成功、`201` 已创建、`204` 成功但无响应体、`400` 参数不合法、
`404` 资源不存在、`409` 数据冲突（邮箱重复）。

## 技术栈

| 组件 | 版本 / 说明 |
|---|---|
| Java | 22（Gradle toolchain 固定，机器上装了 JDK 22 即可） |
| Spring Boot | 3.4.0 |
| Spring Web | REST 控制器、内置 Tomcat |
| Spring Data JPA | 实体映射 + Repository 自动实现 |
| Hibernate | 6.x，JPA 的具体实现 |
| H2 | 2.3.x 内存数据库，启动自动建表 |
| Bean Validation | `spring-boot-starter-validation`，支撑 `@NotBlank`、`@Email`、`@Valid` |
| Gradle | 9.6.0（用项目自带的 `gradlew`，不用单独装） |

## 项目结构

```text
blogsystem-backend/
├── build.gradle                     # 依赖与 Java 版本
├── settings.gradle                  # rootProject.name = 'blogsystem-backend'
├── gradlew / gradlew.bat            # Gradle Wrapper
├── README.md                        # 本文件：接口、运行、改造说明
├── 代码导读.md                       # 逐个文件、逐个注解讲清代码在干什么
└── src/
    ├── main/
    │   ├── java/me/scpark/blogsystem/
    │   │   ├── BlogsystemApplication.java      # 启动类，组件扫描的起点
    │   │   ├── config/CorsConfig.java          # 开发期跨域配置
    │   │   ├── controller/                     # 接请求、发响应，不写业务逻辑
    │   │   │   ├── ArticleController.java
    │   │   │   ├── CommentController.java
    │   │   │   ├── MemberController.java
    │   │   │   └── ApiExceptionHandler.java    # 统一异常 → 400/409
    │   │   ├── service/                        # 业务规则
    │   │   │   ├── ArticleService.java
    │   │   │   ├── CommentService.java
    │   │   │   └── MemberService.java
    │   │   ├── repository/                     # 只跟数据库打交道
    │   │   │   ├── ArticleRepository.java
    │   │   │   ├── CommentRepository.java
    │   │   │   └── MemberRepository.java
    │   │   ├── domain/                         # 实体，一个类对应一张表
    │   │   │   ├── Article.java
    │   │   │   ├── Comment.java
    │   │   │   └── Member.java
    │   │   └── dto/PageResult.java             # 分页响应结构
    │   └── resources/
    │       ├── application.properties          # 端口、数据源、JPA、data.sql
    │       ├── data.sql                        # 预置 3 成员 / 4 文章 / 4 评论
    │       └── static/index.html               # 接口索引页
    └── test/
        ├── java/me/scpark/blogsystem/
        │   ├── JunitTest.java                  # 上下文启动 + data.sql 校验
        │   ├── controller/*Test.java           # MockMvc 接口测试
        │   └── repository/*Test.java           # @DataJpaTest 查询测试
        └── resources/*.sql                     # 测试专用数据
```

## 数据模型

```text
members（站内成员 / 作者）
  id           bigint 主键，自增
  name         姓名，非空
  email        邮箱，非空且唯一

articles（文章）
  id           bigint 主键，自增
  title        标题，非空，≤200
  content      正文，非空，CLOB
  author       作者名，非空（存名字，不是外键）
  tags         标签，逗号分隔，≤200，可为空
  published    是否已发布，非空，默认 true
  created_at   创建时间，非空，创建后不可改
  updated_at   最后修改时间，非空，每次更新自动刷新

comments（评论）
  id           bigint 主键，自增
  article_id   外键 → articles.id，非空
  author       评论人昵称，非空
  content      评论内容，非空，≤1000
  created_at   评论时间，非空
```

两个设计取舍写在代码注释里，这里也重复一遍：

- **文章的 author 是字符串，不是外键。** 博客里作者改名、成员被删都不应该影响历史文章，
  而且文章列表接口也就不用每次都 join members 表。
- **Article 没有反向的 comments 集合。** 评论通过 `Comment.article` 单向关联文章，
  查评论时走 `findByArticle_IdOrderByCreatedAtAsc`。少一层双向关联，就少一类循环引用和懒加载的坑。

## 一次请求走完四层

以 `GET /api/articles?page=0&size=2` 为例：

```text
浏览器 / Postman
      │  GET /api/articles?page=0&size=2
      ▼
内置 Tomcat → DispatcherServlet（按 URL 找到处理方法）
      ▼
ArticleController.findAll(page=0, size=2)        controller：接请求、转参数
      ▼
ArticleService.findPage(0, 2, null)              service：整理分页参数、选查询分支
      ▼
ArticleRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 2))
      │  repository：接口没写实现，Spring 启动时生成
      ▼
Hibernate → select ... from articles order by created_at desc limit 2 offset 0
      │            + select count(*) from articles
      ▼
PageResult.of(page) → Jackson 序列化成 JSON
      ▼
200 OK + {"content":[...], "page":0, "size":2, "totalElements":4, ...}
```

每一层的职责、为什么这么拆、每个注解什么时候生效，见 [代码导读.md](代码导读.md)。

## 数据库配置

`src/main/resources/application.properties` 里几项关键配置：

```properties
spring.datasource.url=jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true
spring.sql.init.encoding=UTF-8
spring.h2.console.enabled=true
spring.jpa.open-in-view=false
```

- `mem:blogdb`：数据库在内存里，**程序一重启数据就回到 `data.sql` 的初始状态**，这是有意为之。
- `DB_CLOSE_DELAY=-1`：连接池回收空闲连接时别把库也一起销毁了。
- `create-drop`：启动时按实体类建表，关闭时删表，不会出现表结构和实体类对不上的情况。
- `defer-datasource-initialization=true`：让 `data.sql` 等 Hibernate 建完表再执行，否则报「表不存在」。
- `open-in-view=false`：事务在 Service 层结束，不在「序列化 JSON」阶段偷偷查库。

`data.sql` 是直接执行的 SQL，不经过实体的 `@PrePersist`，
所以 `articles.created_at` / `updated_at` 这两个非空列必须自己写值。

换成 MySQL 时：改数据源四行配置、把 H2 驱动换成 MySQL 驱动、把 `ddl-auto` 改成 `update`（或交给 Flyway），
实体、Service、Controller 一行都不用动 —— 这就是分层的意义。

## 运行项目

```bash
# 在项目根目录
./gradlew bootRun
```

Windows 命令提示符 / PowerShell 里：

```powershell
.\gradlew.bat bootRun
```

如果 `JAVA_HOME` 没配，先指定 JDK 22：

```powershell
$env:JAVA_HOME = "D:\Dev\SDK\Java\jdk-22.0.2"
.\gradlew.bat bootRun
```

启动后打开 <http://localhost:8080/> 看接口索引页。

## 接口用法示例

浏览器地址栏只能发 GET，POST / PUT / PATCH / DELETE 请用 Postman、curl 或下面的 PowerShell。

新建一篇文章：

```powershell
$body = @{
    title     = "第一次用 PowerShell 调接口"
    content   = "请求体里的 JSON，会先被校验，再存进数据库。"
    author    = "张三"
    tags      = "PowerShell,测试"
    published = $true
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/articles" `
    -ContentType "application/json; charset=utf-8" `
    -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
```

只看草稿、每页 5 条：

```powershell
Invoke-RestMethod "http://localhost:8080/api/articles?published=false&size=5"
```

给 1 号文章发评论：

```powershell
$comment = @{ author = "李四"; content = "这条评论会挂在文章 1 下面。" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/articles/1/comments" `
    -ContentType "application/json; charset=utf-8" `
    -Body ([System.Text.Encoding]::UTF8.GetBytes($comment))
```

把 4 号草稿发布出去：

```powershell
Invoke-RestMethod -Method Post "http://localhost:8080/api/articles/4/publish"
```

用 curl 也一样：

```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title":"curl 新建","content":"正文","author":"张三"}'
```

## 测试

```bash
./gradlew test        # 只跑测试
./gradlew build       # 编译 + 测试 + 打包成 jar
```

| 测试类 | 测什么 |
|---|---|
| `JunitTest` | 整个 Spring 容器能起来；Bean 都在；H2 连得上；`data.sql` 的成员/文章/评论确实入库 |
| `ArticleControllerTest` | 列表分页、草稿过滤、标题搜索、详情 404、创建 201、校验 400、PATCH、发布、删除 |
| `CommentControllerTest` | 查评论、文章不存在 404、发评论、内容为空 400、删评论 |
| `MemberApiTest` | 成员列表、邮箱格式 400、邮箱重复 409 |
| `ArticleRepositoryTest` | 分页排序、发布状态过滤、标题模糊搜索、时间戳自动填充 |
| `MemberRepositoryTest` | 成员派生查询、新增、删除（`@DataJpaTest`，跑完自动回滚） |
| `MemberControllerTest` | 不启动 Spring，用 mock Service 直接测控制器方法 |

`@SpringBootTest` 测试用的是 MOCK 环境，不占 8080 端口，所以项目正在运行时也能跑测试。

## 从学习项目改造过来做了什么

| 项 | 改造前 | 改造后 |
|---|---|---|
| 项目名 | `springboot-devloper` | `blogsystem-backend` |
| 包名 / 启动类 | `me.scpark.springdeveloper` / `BackendApplication` | `me.scpark.blogsystem` / `BlogsystemApplication` |
| 文章实体 | 只有 id/title/content/author/createdAt，没有接口 | 加 tags、published、updatedAt，接口齐全 |
| 评论 | 无 | `Comment` 实体 + 关联查询 + 完整接口 |
| 列表接口 | 一次全查 | 分页 + `PageResult` 包装，`page`/`size` 有上限 |
| 参数校验 | 无 | Bean Validation + `@Valid`，错误统一 400 |
| 异常处理 | 默认错误页 / 500 | `ApiExceptionHandler`：400 带字段错误、409 处理唯一约束 |
| 跨域 | 无 | `/api/**` 允许跨域，前端可直连 |
| 演示接口 | `/request`、`/hi`、`/test`、`/quiz` | 已删除（它们属于学习阶段，不是博客业务） |
| 示例数据 | 3 个成员 | 3 个成员 + 4 篇文章（含 1 篇草稿）+ 4 条评论 |

原来的 chapter0～chapter4 分支和历史提交都还在，用 `git log` / `git switch chapter4` 可以回看。
本次改造在 `blogsystem-backend` 分支上。

## 常见问题

**启动报「Table "ARTICLES" not found」**
`spring.jpa.defer-datasource-initialization=true` 少了，`data.sql` 在建表之前就跑了。

**中文变成问号 / 乱码**
- `data.sql`：检查 `spring.sql.init.encoding=UTF-8`；
- 用 PowerShell 发请求：`Invoke-RestMethod` 的 body 要显式转成 UTF-8 字节，见上面的示例；
- Windows 控制台显示乱码不代表数据错了，去 `/h2-console` 里看一眼最准。

**GET /api/articles 拿不到刚创建的文章**
新建文章默认 `published=true`，但如果请求里显式传了 `"published": false`，它会进草稿，
默认列表不筛选，所以能看到；如果你加了 `?published=true`，草稿就被过滤掉了。

**页码对不上**
`page` 从 0 开始。前端显示「第 1 页」时，请求要传 `page=0`。

**PUT 之后文章变回草稿了**
`published` 是基本类型 `boolean`，PUT 不传这个字段时按 `false` 处理。
要保留发布状态，PUT 请求体里就要带上 `"published": true`，或者改用 PATCH / 发布接口。

**端口 8080 被占用**
改 `application.properties` 里的 `server.port`，或者关掉占用的进程。

## 后续计划

- [ ] 登录与鉴权（Spring Security + JWT），成员表加密码哈希和角色。
- [ ] 标签拆成独立表，支持按标签检索和标签云。
- [ ] 评论审核状态（草稿里那篇文章就是这个思路）。
- [ ] 文章封面图上传与对象存储。
- [ ] 换成 MySQL / PostgreSQL，并用 Flyway 管理表结构变更。
- [ ] 接口文档化（springdoc-openapi，自动生成 Swagger UI）。
