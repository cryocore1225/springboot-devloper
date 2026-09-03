# springboot-devloper

一个用于学习 **Spring Boot Java Web 开发** 的入门项目。项目通过 Gradle 管理依赖，当前包含 Spring Boot 启动类、REST 接口和静态首页，用来熟悉从项目启动到 HTTP 请求响应的基本流程。

## 目录

- [项目简介](#项目简介)
- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心代码说明](#核心代码说明)
- [Spring Boot 启动流程](#spring-boot-启动流程)
- [接口文档](#接口文档)
- [运行项目](#运行项目)
- [构建项目](#构建项目)
- [Gradle 配置说明](#gradle-配置说明)
- [常见问题](#常见问题)
- [后续开发计划](#后续开发计划)

## 项目简介

项目名称是 `springboot-devloper`，包名为 `me.scpark.springdeveloper`。当前代码重点学习以下内容：

1. 如何创建 Spring Boot 应用。
2. 如何使用控制器接收浏览器发送的 HTTP 请求。
3. 如何使用 `@GetMapping` 定义 GET 接口。
4. 如何返回文本响应。
5. 如何使用 Spring Boot 提供静态 HTML 页面。
6. 如何使用 Gradle Wrapper 构建和运行项目。

## 功能概览

| 功能 | 地址 | 说明 |
|---|---|---|
| 静态首页 | `GET /` | 显示项目欢迎页面 |
| 通用映射接口 | `GET /request` | 使用 `@RequestMapping` 指定 GET 请求 |
| 问候接口 | `GET /hi` | 使用 `@GetMapping` 返回中文问候文本 |
| 测试接口 | `GET /test` | 使用 `@GetMapping` 返回测试文本 |
| 创建用户 | `POST /users` | 使用 `@PostMapping` 创建示例数据 |
| 修改用户 | `PUT /users/{id}` | 使用 `@PutMapping` 整体修改示例数据 |
| 部分修改用户 | `PATCH /users/{id}` | 使用 `@PatchMapping` 局部修改示例数据 |
| 删除用户 | `DELETE /users/{id}` | 使用 `@DeleteMapping` 删除示例数据 |

## 技术栈

- **Java 22**：项目配置的 Java 工具链版本。
- **Spring Boot 3.4.0**：应用启动和 Web 开发框架。
- **Spring Web**：提供 REST 控制器和 HTTP 请求处理能力。
- **Gradle 9.6.0**：项目构建与依赖管理工具。
- **Gradle Wrapper**：使用项目自带的 Gradle 版本，不需要单独安装 Gradle。
- **IntelliJ IDEA**：推荐的 Java 开发工具。

## 项目结构

```text
springboot-devloper/
├── .gitignore                         # Git 忽略规则
├── build.gradle                       # Gradle 插件、版本和依赖配置
├── settings.gradle                    # Gradle 项目名称配置
├── gradlew                            # Linux/macOS Gradle Wrapper
├── gradlew.bat                        # Windows Gradle Wrapper
├── gradle/
│   └── wrapper/                       # Gradle Wrapper 文件
└── src/
    └── main/
        ├── java/
        │   └── me/scpark/springdeveloper/
        │       ├── BackendApplication.java       # Spring Boot 启动类
        │       └── TestController.java           # 示例 REST 控制器
        └── resources/
            └── static/
                └── index.html                    # 静态首页
```

构建后还会生成 `build/` 目录。该目录是编译产物和临时构建文件，已经被 `.gitignore` 忽略，不应提交到 Git。

## 核心代码说明

### `BackendApplication.java`

```java
@SpringBootApplication // 启用自动配置，并扫描当前包下的 Spring 组件。
public class BackendApplication {
    public static void main(String[] args) {
        // 启动 Spring Boot，同时启动内置的 Web 服务器。
        SpringApplication.run(BackendApplication.class, args);
    }
}
```

- `@SpringBootApplication` 是 Spring Boot 应用的核心注解。
- 它组合了配置、自动配置和组件扫描功能。
- `main` 方法是 Java 程序入口。
- `SpringApplication.run(...)` 会创建 Spring 容器并启动内置 Web 服务器。
- 默认情况下，应用监听 `8080` 端口。

### `TestController.java`

```java
@RestController // 方法返回值会直接作为 HTTP 响应内容。
public class TestController {
    // @RequestMapping 是通用写法，可以通过 method 指定请求类型。
    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String requestMappingExample() {
        return "这是 @RequestMapping 接口返回的内容。";
    }

    @GetMapping("/hi") // 处理 GET 请求，通常用于查询数据。
    public String hi() {
        return "你好，这是 Spring Boot 返回的内容。";
    }

    @PostMapping("/users") // 处理 POST 请求，通常用于创建数据。
    public String createUser() {
        return "用户创建成功";
    }

    @PutMapping("/users/{id}") // 处理 PUT 请求，通常用于整体修改数据。
    public String updateUser(@PathVariable int id) {
        return "修改用户 " + id;
    }

    @PatchMapping("/users/{id}") // 处理 PATCH 请求，通常用于局部修改数据。
    public String patchUser(@PathVariable int id) {
        return "部分修改用户 " + id;
    }

    @DeleteMapping("/users/{id}") // 处理 DELETE 请求，通常用于删除数据。
    public String deleteUser(@PathVariable int id) {
        return "删除用户 " + id;
    }
}
```

- `@RestController` 表示这是一个 REST 控制器，方法返回值会直接作为 HTTP 响应内容。
- `@RequestMapping` 是通用映射注解，可以通过 `method` 属性指定请求类型。
- `@GetMapping` 处理 GET 请求，通常用于查询数据。
- `@PostMapping` 处理 POST 请求，通常用于创建数据。
- `@PutMapping` 处理 PUT 请求，通常用于整体修改数据。
- `@PatchMapping` 处理 PATCH 请求，通常用于局部修改数据。
- `@DeleteMapping` 处理 DELETE 请求，通常用于删除数据。
- `{id}` 是路径参数，`@PathVariable` 可以把它接收到 Java 方法参数中。
- 方法返回 `String` 时，浏览器或客户端会收到普通文本响应。

### `index.html`

文件位置：

```text
src/main/resources/static/index.html
```

Spring Boot 会自动提供 `static` 目录下的静态资源。访问根路径 `/` 时，应用会返回这个 HTML 页面。该页面与 REST 接口是两种不同的内容：

- `/` 返回 HTML 页面。
- `/hi`、`/test` 和 `/request` 是 GET 文本接口。
- `/users` 相关接口分别演示 POST、PUT、PATCH 和 DELETE 请求。

## Spring Boot 启动流程

```text
运行 BackendApplication.main()
          │
          ▼
SpringApplication.run(...)
          │
          ▼
创建 Spring ApplicationContext
          │
          ▼
扫描 me.scpark.springdeveloper 包
          │
          ▼
发现 TestController 和请求映射
          │
          ▼
启动内置 Web 服务器（默认 8080）
          │
          ▼
浏览器访问 /、/hi 或 /test
```

由于启动类和控制器位于同一个基础包及其子包范围内，Spring Boot 可以自动发现 `TestController`。

## 接口文档

### 1. 首页

```http
GET http://localhost:8080/
```

返回 `index.html` 静态页面，页面内容包括项目标题和欢迎信息。

### 2. `@RequestMapping` 示例

```http
GET http://localhost:8080/request
```

返回：

```text
这是 @RequestMapping 接口返回的内容。
```

### 3. `/hi` 问候接口

```http
GET http://localhost:8080/hi
```

返回：

```text
你好，这是 Spring Boot 返回的内容。
```

### 4. `/test` 测试接口

```http
GET http://localhost:8080/test
```

返回：

```text
你好，这是 /test 接口返回的内容。
```

### 5. 用户接口示例

下面四个接口只用于演示不同的 HTTP 请求映射方式，暂时没有连接数据库。

```text
POST   /users       -> 用户创建成功
PUT    /users/1     -> 修改用户 1
PATCH  /users/1     -> 部分修改用户 1
DELETE /users/1     -> 删除用户 1
```

PowerShell 可以使用 `Invoke-WebRequest` 发送 GET 请求；POST、PUT、PATCH 和 DELETE 请求可以使用 Postman、IDEA HTTP Client 或 curl 测试。

例如：

```powershell
Invoke-WebRequest -Method Post http://localhost:8080/users
Invoke-WebRequest -Method Put http://localhost:8080/users/1
Invoke-WebRequest -Method Patch http://localhost:8080/users/1
Invoke-WebRequest -Method Delete http://localhost:8080/users/1
```

也可以使用 PowerShell 测试 GET 接口：

```powershell
Invoke-WebRequest http://localhost:8080/request
Invoke-WebRequest http://localhost:8080/hi
Invoke-WebRequest http://localhost:8080/test
```

## 运行项目

### 使用 Gradle Wrapper 启动

进入项目根目录后，在 Windows PowerShell 中执行：

```powershell
.\gradlew.bat bootRun
```

启动成功后访问：

```text
http://localhost:8080/
http://localhost:8080/hi
http://localhost:8080/test
```

### 使用 IntelliJ IDEA 启动

1. 使用 IntelliJ IDEA 打开项目目录。
2. 等待 Gradle 同步完成。
3. 打开 `BackendApplication.java`。
4. 运行 `main` 方法。
5. 在浏览器中访问接口地址。

## 构建项目

执行完整构建：

```powershell
.\gradlew.bat build
```

清理后重新构建：

```powershell
.\gradlew.bat clean build
```

只编译主程序：

```powershell
.\gradlew.bat classes
```

构建结果：

- 编译后的类文件位于 `build/classes/`。
- 资源文件位于 `build/resources/`。
- Spring Boot 可执行 JAR 位于 `build/libs/`。

## Gradle 配置说明

`build.gradle` 主要包含以下配置：

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.0'
    id 'io.spring.dependency-management' version '1.1.0'
}
```

- `java`：启用 Java 编译功能。
- `org.springframework.boot`：启用 Spring Boot 的构建和运行任务。
- `io.spring.dependency-management`：统一管理 Spring 相关依赖版本。

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}
```

表示项目使用 Java 22 工具链编译。运行项目时，应确保本机可以使用 Java 22，或让 Gradle Toolchain 自动找到可用的 JDK。

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

- `implementation`：主程序运行所需依赖。
- `testImplementation`：测试代码使用的依赖。
- `spring-boot-starter-web`：提供 Spring MVC、内置服务器和 JSON/HTTP 支持。
- `spring-boot-starter-test`：提供 Spring Boot 测试常用工具。

## 常见问题

### 端口被占用

如果 `8080` 端口已经被其他程序占用，启动会失败。可以先查找并结束占用端口的进程，或者在后续添加 `application.properties` 后修改端口。

### 访问接口返回 404

优先检查：

1. Spring Boot 应用是否已经成功启动。
2. 请求方法是否为 GET。
3. URL 是否正确：`/hi` 和 `/test` 区分大小写。
4. `TestController` 是否位于启动类基础包或其子包下。

### Gradle 找不到构建文件

必须在项目根目录执行 Gradle 命令，也就是同时包含以下文件的目录：

```text
build.gradle
settings.gradle
gradlew.bat
```

### 编译失败

优先检查：

- Java 版本是否满足项目配置的 Java 22。
- Gradle 依赖是否能够访问 Maven Central。
- `build.gradle` 是否存在拼写错误。

## 后续开发计划

后续可以按以下顺序扩展项目：

1. 增加 `application.properties`，学习应用配置和端口修改。
2. 将控制器整理到 `controller` 包中。
3. 增加 `User` 数据模型和 `/users` 接口。
4. 学习 POST、PUT、DELETE 请求。
5. 增加 Service 层，分离控制器和业务逻辑。
6. 增加 H2 数据库和 Spring Data JPA。
7. 实现用户数据的增删改查（CRUD）。
8. 添加 `MockMvc` 接口测试。
9. 增加统一异常处理和参数校验。

## Git 信息

当前开发分支：

```text
chapter3
```

远程仓库：

```text
https://github.com/cryocore1225/springboot-devloper.git
```

提交代码前建议执行：

```powershell
.\gradlew.bat build
git status
git diff --check
```
