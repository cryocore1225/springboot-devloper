# Blogsystem backend

Spring Boot 博客后端（入门版）：一篇文章新增接口、一个文章列表接口。

## 技术栈

Java 22、Spring Boot 3.4、Spring Data JPA、H2 内存数据库、Lombok、Gradle。

## 代码结构

```text
src/main/java/me/scpark/blogsystem/
├── BlogsystemApplication.java          启动类
├── controller/BlogApiController.java   接口
├── service/BlogService.java            业务
├── repository/BlogRepository.java      仓库
├── domain/Article.java                 文章表
├── dto/AddArticleRequest.java          新增文章的请求数据
└── dto/ArticleResponse.java            列表接口返回的数据
```

## 运行

```bash
./gradlew bootRun
```

Windows 命令行：

```powershell
.\gradlew.bat bootRun
```

启动后打开 <http://localhost:8080/>。

## 接口

新增文章（Postman：POST，Body → raw → JSON）：

```json
{"title": "第一篇", "content": "第一篇文章的正文"}
```

返回 201：

```json
{"id": 1, "title": "第一篇", "content": "第一篇文章的正文"}
```

查询全部文章（Postman：GET）：

```json
[{"id": 1, "title": "第一篇", "content": "第一篇文章的正文"}]
```

没有文章时返回 `[]`。

删除文章（Postman：DELETE，URL 里带 id）：

```text
DELETE /api/articles/1
```

删除成功返回 **204**（没有响应体）；id 不存在返回 **404**。

## 数据库

H2 内存库，JDBC URL 是 `jdbc:h2:mem:blogdb`，用户名 `sa`，密码为空。
程序关掉数据就没了，重启后是一张空表。
浏览器打开 <http://localhost:8080/h2-console> 可以直接查 `article` 表。
