# springboot-devloper

一个用于学习 **Spring Boot Java Web 开发** 的入门项目。项目通过 Gradle 管理依赖，包含 Spring Boot 启动类、REST 接口和静态首页，并已接入 **H2 内存数据库 + Spring Data JPA**，实现了一套真正读写数据库的用户增删改查接口，用来熟悉从项目启动、HTTP 请求响应到数据落库的完整流程。

## 目录

- [项目简介](#项目简介)
- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心代码说明](#核心代码说明)
- [数据库配置说明](#数据库配置说明)
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
3. 如何使用 `@GetMapping`、`@PostMapping` 等注解定义不同请求方法的接口。
4. 如何返回文本响应和 JSON 响应。
5. 如何使用 Spring Boot 提供静态 HTML 页面。
6. 如何使用 Gradle Wrapper 构建和运行项目。
7. 如何用 Spring Data JPA 把 Java 类映射成数据库表（`@Entity`）。
8. 如何只写一个继承 `JpaRepository` 的接口就获得全套增删改查方法。
9. 如何按 controller / service / repository / entity 四层组织代码。
10. 如何用 `application.properties` 配置数据源，并用 `data.sql` 预置初始数据。

## 功能概览

| 功能 | 地址 | 说明 |
|---|---|---|
| 静态首页 | `GET /` | 显示项目欢迎页面 |
| 通用映射接口 | `GET /request` | 使用 `@RequestMapping` 指定 GET 请求 |
| 问候接口 | `GET /hi` | 使用 `@GetMapping` 返回中文问候文本 |
| 测试接口 | `GET /test` | 使用 `@GetMapping` 返回测试文本 |
| 查询全部用户 | `GET /users` | 查出数据库里的所有用户 |
| 查询单个用户 | `GET /users/{id}` | 按 id 查询，查不到返回 404 |
| 新增用户 | `POST /users` | 把请求体里的 JSON 存进数据库，返回 201 |
| 整体替换用户 | `PUT /users/{id}` | 用请求体整体覆盖原有数据 |
| 局部更新用户 | `PATCH /users/{id}` | 只修改请求体里出现的字段 |
| 删除用户 | `DELETE /users/{id}` | 从数据库删除，成功返回 204 |
| 数据库控制台 | `GET /h2-console` | H2 自带的网页版 SQL 控制台 |

上面除首页和控制台之外的接口都真正读写数据库，不再是返回写死字符串的假接口。

## 技术栈

- **Java 22**：项目配置的 Java 工具链版本。
- **Spring Boot 3.4.0**：应用启动和 Web 开发框架。
- **Spring Web**：提供 REST 控制器和 HTTP 请求处理能力。
- **Spring Data JPA**：用接口和注解完成数据库操作，不用手写 SQL 和实现类。
- **Hibernate 6.6**：JPA 的具体实现，负责把实体类翻译成建表和增删改查语句。
- **H2 2.3.232**：内存数据库，程序一启动就自动建库建表，不需要额外安装。
- **HikariCP**：连接池，由 Spring Boot 自动配置。
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
        │       ├── BackendApplication.java   # Spring Boot 启动类
        │       ├── controller/               # 控制器层：接收 HTTP 请求
        │       │   ├── TestController.java       # 文本接口示例
        │       │   └── UserController.java       # 用户增删改查接口
        │       ├── service/                  # 业务逻辑层：决定要做什么
        │       │   └── UserService.java
        │       ├── repository/               # 数据访问层：和数据库打交道
        │       │   └── UserRepository.java
        │       └── entity/                   # 实体层：Java 类与数据表的映射
        │           └── User.java
        └── resources/
            ├── application.properties        # 应用与数据库配置
            ├── data.sql                      # 启动时预置的示例数据
            └── static/
                └── index.html                # 静态首页
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

}
```

- `@RestController` 表示这是一个 REST 控制器，方法返回值会直接作为 HTTP 响应内容。
- `@RequestMapping` 是通用映射注解，可以通过 `method` 属性指定请求类型。
- `@GetMapping` 处理 GET 请求，通常用于查询数据；它其实就是 `@RequestMapping(method = RequestMethod.GET)` 的简写。
- 方法返回 `String` 时，浏览器或客户端会收到普通文本响应。

这个控制器现在只保留三个不访问数据库的文本接口。原来演示 POST / PUT / PATCH / DELETE 的四个方法已经删掉，
因为它们的路径 `/users` 会和新写的 `UserController` 撞车，改由后者提供同样路径、但真正操作数据库的版本。

### `index.html`

文件位置：

```text
src/main/resources/static/index.html
```

Spring Boot 会自动提供 `static` 目录下的静态资源。访问根路径 `/` 时，应用会返回这个 HTML 页面。该页面与 REST 接口是两种不同的内容：

- `/` 返回 HTML 页面。
- `/hi`、`/test` 和 `/request` 是 GET 文本接口。
- `/users` 相关接口真正读写数据库，覆盖查询、新增、整体修改、局部修改和删除。
- `/h2-console` 是 H2 数据库自带的控制台页面。

### `entity/User.java`

```java
@Entity // 告诉 JPA：这是一个要映射成数据表的类。
@Table(name = "users") // user 是 H2 的保留字，表名直接叫 user 会建表失败，所以用复数。
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    protected User() { }   // JPA 要求的无参构造函数
}
```

- `@Entity` 把这个类和一张数据表绑定起来，Hibernate 会根据字段自动建表。
- `@Table(name = "users")` 指定表名。**不能直接用 `user`**，它是 H2 的保留字，建表会失败。
- `@Id` 标记主键；`@GeneratedValue(strategy = GenerationType.IDENTITY)` 表示主键交给数据库自增生成，新增时不用自己填。
- `@Column` 用来设置列的限制：`nullable = false` 表示不允许为空，`unique = true` 表示不允许重复。
- 无参构造函数是 JPA 的硬性要求：Hibernate 从数据库读出数据时，要用反射先造一个空对象再往里塞值。
- 这里没有引入 Lombok，getter 和 setter 都是手写的，学习阶段看得见每一步更好。

关于列名：Spring Boot 默认的命名策略会把驼峰字段名转成下划线列名，
所以 `name`、`email` 这类单个单词的字段列名不变，但以后如果加上 `createdAt`，
建表时对应的列名会是 `created_at` —— 这是正常行为，不是配置写错了。

### `repository/UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

- 只要继承 `JpaRepository` 并写好泛型，Spring Data JPA 就会在启动时自动生成实现类，
  `findAll`、`findById`、`save`、`deleteById`、`existsById` 这些方法全都不用手写。
- 两个泛型参数分别是「要操作的实体类」和「它的主键类型」。
- `findByEmail` 演示了**派生查询**：方法名本身就是一条查询语句，`findBy` 后面跟字段名，
  Spring Data JPA 会把它翻译成 `where email = ?`。它目前还没有对应的接口，留作写法示例。

### `service/UserService.java`

业务逻辑层。控制器只负责收发 HTTP 请求，具体做什么由这一层决定。

- `@Service` 把它标记成由 Spring 管理的服务组件。
- `@Transactional(readOnly = true)` 加在类上，表示下面的方法默认运行在只读事务里；写数据的方法各自覆盖成可写事务。
- 通过**构造函数注入** `UserRepository`，字段声明成 `final`。只有一个构造函数时不需要写 `@Autowired`。
- 查不到用户时统一返回 `Optional.empty()`，交给控制器去决定返回 404，而不是直接抛异常。

写方法有三个值得注意的地方：

1. `create` 调用 `save`。`save` 会根据主键是否为空自行判断是新增还是更新：主键为空就是新增，生成 `insert` 语句。
2. `update`（PUT）和 `patch`（PATCH）都**没有**调用 `save`。从数据库查出来的对象已经处于 Hibernate 的「托管」状态，
   方法结束、事务提交时，Hibernate 会自动比对字段有没有变化，变了就生成 `update` 语句 —— 这叫脏检查。
3. `patch` 里每个字段都先判断是否为 null 再赋值，这正是 PUT 和 PATCH 语义差别的来源：
   PUT 无条件覆盖全部字段，PATCH 只覆盖请求里真正提供了的字段。

### `controller/UserController.java`

```java
@RestController
@RequestMapping("/users") // 类级前缀，下面每个方法只写自己那一段路径
public class UserController {
    @GetMapping              public ResponseEntity<List<User>> findAll()
    @GetMapping("/{id}")     public ResponseEntity<User> findById(@PathVariable Long id)
    @PostMapping             public ResponseEntity<User> create(@RequestBody User user)
    @PutMapping("/{id}")     public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user)
    @PatchMapping("/{id}")   public ResponseEntity<User> patch(@PathVariable Long id, @RequestBody User user)
    @DeleteMapping("/{id}")  public ResponseEntity<Void> delete(@PathVariable Long id)
}
```

- `@RequestMapping("/users")` 是类级前缀，和方法上的路径拼起来才是完整地址。
- `@RequestBody` 把请求体里的 JSON 自动转成 `User` 对象，`@PathVariable` 把 URL 里的 `{id}` 接进方法参数。
- 返回值用 `ResponseEntity` 而不是裸的 `User`，因为需要表达不同的状态码：
  - 新增成功返回 **201 Created**，并通过 `Location` 响应头告诉调用方新资源的地址。
  - 删除成功返回 **204 No Content**，表示成功但没有响应体。
  - 查不到用户返回 **404 Not Found**，用 `Optional` 的 `map` / `orElseGet` 一行写完。
- PUT 与 PATCH 的区别：PUT 会把没提供的字段一并覆盖成 null，PATCH 只改提供的字段。这个差别可以在下面的接口文档里实测。

## 数据库配置说明

### `application.properties`

Spring Boot 的配置文件，放在 `src/main/resources/` 下会被自动读取。本项目用到的主要配置：

| 配置项 | 值 | 作用 |
|---|---|---|
| `server.port` | `8080` | 内置 Web 服务器的端口 |
| `spring.datasource.url` | `jdbc:h2:mem:springbootdb;DB_CLOSE_DELAY=-1` | H2 内存库的连接地址 |
| `spring.h2.console.enabled` | `true` | 打开 H2 网页版控制台 |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | 启动时按实体类建表，关闭时删表 |
| `spring.jpa.show-sql` | `true` | 把 Hibernate 生成的 SQL 打印到控制台 |
| `spring.jpa.defer-datasource-initialization` | `true` | 让 `data.sql` 在建表之后才执行 |
| `spring.sql.init.encoding` | `UTF-8` | 按 UTF-8 读取 `data.sql`，避免中文乱码 |

两个容易踩坑的配置：

- **`DB_CLOSE_DELAY=-1` 必须加。** H2 内存库默认会在最后一个连接关闭时被销毁，
  而连接池会回收空闲连接，不加这个参数，数据可能在程序运行途中凭空消失。
  `-1` 表示数据库一直存活到 JVM 退出。
- **`ddl-auto` 用 `create-drop` 而不是 `update`。** 内存库每次重启本来就是空的，
  `create-drop` 语义更直白，而且启动时一定按实体类重新建表，不会出现表结构和实体类对不上的情况。

### `data.sql`

预置示例数据的脚本，插入了三条用户记录（张三、李四、王五），这样程序一启动 `GET /users` 就有内容可看。

**这里有个经典坑**：Spring Boot 默认在**数据源初始化阶段**执行 `data.sql`，而这个时机**早于 Hibernate 建表**，
直接 INSERT 会报「表不存在」。解决办法就是配上 `spring.jpa.defer-datasource-initialization=true`，
把脚本执行推迟到建完表之后。这两个文件必须成对存在，缺一不可。

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
扫描 me.scpark.springdeveloper 包及其子包
          │
          ▼
发现 controller / service / repository / entity 下的各个组件
          │
          ▼
创建 HikariCP 连接池，连上 H2 内存库
          │
          ▼
Hibernate 按 User 实体建出 users 表
          │
          ▼
执行 data.sql，插入三条示例数据
          │
          ▼
启动内置 Web 服务器（默认 8080）
          │
          ▼
浏览器访问 /、/hi 或 /users
```

启动类 `BackendApplication` 位于基础包 `me.scpark.springdeveloper`，
而 `controller`、`service`、`repository`、`entity` 都是它的子包，
所以组件扫描和实体扫描都能自动发现它们，不需要额外配置。

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

### 5. 用户接口

下面这些接口会真正读写数据库。程序启动后 H2 里已经有三条预置数据。

**查询全部用户**

```http
GET http://localhost:8080/users
```

```json
[
  {"id":1,"name":"张三","email":"zhangsan@example.com"},
  {"id":2,"name":"李四","email":"lisi@example.com"},
  {"id":3,"name":"王五","email":"wangwu@example.com"}
]
```

**按 id 查询单个用户**

```http
GET http://localhost:8080/users/1
```

查到返回 200 和该用户；id 不存在返回 404。

**新增用户**

```http
POST http://localhost:8080/users
Content-Type: application/json

{"name":"赵六","email":"zhaoliu@example.com"}
```

返回 **201 Created**，响应体是带上数据库自增 id 的新用户，`Location` 响应头指向 `/users/4`。

**整体替换用户（PUT）**

```http
PUT http://localhost:8080/users/1
Content-Type: application/json

{"name":"李四改名","email":"lisi-new@example.com"}
```

name 和 email 都会被请求体里的值覆盖。**PUT 必须提供全部字段**，漏掉的字段会被置为 null，
触发数据库的非空约束而返回 500，详见下面的常见问题。

**局部更新用户（PATCH）**

```http
PATCH http://localhost:8080/users/1
Content-Type: application/json

{"name":"王五"}
```

只修改 name，email 保持原样 —— 这就是 PUT 和 PATCH 最直观的区别。

**删除用户**

```http
DELETE http://localhost:8080/users/1
```

成功返回 **204 No Content**（没有响应体）；该用户不存在返回 404。

**用 PowerShell 测试写操作**

POST、PUT、PATCH、DELETE 没法直接用浏览器地址栏访问，可以用 PowerShell 的 `Invoke-WebRequest`：

```powershell
Invoke-WebRequest http://localhost:8080/users

Invoke-WebRequest -Method Post -Uri http://localhost:8080/users `
  -ContentType "application/json" -Body '{"name":"赵六","email":"zhaoliu@example.com"}'

Invoke-WebRequest -Method Put -Uri http://localhost:8080/users/1 `
  -ContentType "application/json" -Body '{"name":"李四改名","email":"lisi-new@example.com"}'

Invoke-WebRequest -Method Patch -Uri http://localhost:8080/users/1 `
  -ContentType "application/json" -Body '{"name":"王五"}'

Invoke-WebRequest -Method Delete -Uri http://localhost:8080/users/1
```

注意 PowerShell 5.1 遇到 404 这类非 2xx 状态码会直接抛出异常，那正说明接口按预期返回了 404。

也可以使用 PowerShell 测试 GET 接口：

```powershell
Invoke-WebRequest http://localhost:8080/request
Invoke-WebRequest http://localhost:8080/hi
Invoke-WebRequest http://localhost:8080/test
```

### 6. H2 数据库控制台

```http
GET http://localhost:8080/h2-console
```

用浏览器打开后，按下面的信息登录（**必须与 `application.properties` 里的配置一致**）：

| 字段 | 填什么 |
|---|---|
| JDBC URL | `jdbc:h2:mem:springbootdb` |
| User Name | `sa` |
| Password | 留空 |

注意 JDBC URL **不要**把配置里的 `;DB_CLOSE_DELAY=-1` 一起抄进来。

登录后执行 `SELECT * FROM users;`，就能看到接口增删改之后的真实数据，
也可以自己写 SQL 验证某个接口到底改了什么。

## 运行项目

### 使用 Gradle Wrapper 启动

进入项目根目录后，在 Windows PowerShell 中执行：

```powershell
.\gradlew.bat bootRun
```

启动成功后访问：

```text
http://localhost:8080/             首页
http://localhost:8080/users        用户列表（真实数据）
http://localhost:8080/hi           文本接口
http://localhost:8080/test         文本接口
http://localhost:8080/h2-console   H2 数据库控制台
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
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

- `implementation`：主程序编译和运行都需要的依赖。
- `runtimeOnly`：只在运行程序时才需要的依赖。
- `spring-boot-starter-web`：提供 Spring MVC、内置服务器和 JSON/HTTP 支持。
- `spring-boot-starter-data-jpa`：提供 Spring Data JPA、Hibernate 和 HikariCP 连接池。
- `com.h2database:h2`：H2 数据库驱动，编译时用不到，所以写成 `runtimeOnly` 就够了。
- `spring-boot-starter-test`：提供 Spring Boot 测试常用工具。

## 常见问题

### 端口被占用

如果 `8080` 端口已经被其他程序占用，启动会失败，日志里会出现 `Port 8080 was already in use`。

可以在 PowerShell 里用下面这条命令找到占用端口的进程，再决定是否结束它：

```powershell
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
```

也可以直接改 `application.properties` 里的 `server.port`，换一个端口运行。

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

### H2 控制台连不上或看不到表

优先检查：

1. JDBC URL 是否写成 `jdbc:h2:mem:springbootdb`，能否和 `application.properties` 对上。
2. User Name 是不是 `sa`，密码是不是留空。
3. 应用本身是否还在运行 —— 内存库随程序一起消失，程序关掉后控制台自然也连不上。
4. 表名是 `users` 而不是 `user`。

### PUT 请求返回 500

PUT 是「整体替换」，请求体里必须提供全部字段。只传 `{"name":"..."}` 而漏掉 `email`，
email 会被置为 null，触发数据库的非空约束，于是返回 500。

好消息是数据不会被破坏：`@Transactional` 会让这次操作整体回滚，原来的记录保持不变。
换成 PATCH 只传要改的字段就不会有这个问题。

想从根上避免，正常做法是加 DTO 和 `@Valid` 做参数校验，让非法请求在进入业务逻辑之前就返回 400（见后续开发计划）。

### 用已存在的邮箱新增用户返回 500

`User` 的 `email` 字段标了 `unique = true`，邮箱不允许重复。重复时数据库会抛唯一约束冲突，
而目前还没有参数校验和异常处理，所以客户端拿到的是 500 而不是 400。

和 PUT 漏传字段一样，数据不会被写坏：这次插入会整体回滚，原有记录保持不变。
可以在失败之后再 `GET /users` 确认条数没有变化。

正常做法是加 `@Valid` 参数校验，或者先调 `UserRepository.findByEmail` 查一次再决定是否放行 ——
`UserRepository` 里那个方法就是为这类场景预留的。

### 用 Git Bash 的 curl 发送中文请求体报 400

在 Windows 的 Git Bash 里执行 `curl -d '{"name":"张三"}'`，日志会报
`JSON parse error: Invalid UTF-8 middle byte ...`。这不是接口的问题，
而是 Git Bash 把命令行参数里的中文按 GBK 编码传了出去。

两个解决办法：改用 PowerShell 的 `Invoke-WebRequest`；
或者把 JSON 存成 UTF-8 文件，再用 `curl --data-binary @body.json` 发送。

### 启动日志里的 open-in-view 警告

启动时会看到一条 `spring.jpa.open-in-view is enabled by default` 警告。
这是 Spring Boot 的默认行为，本项目直接返回实体对象、没有用到延迟加载，可以先忽略；
想消除它，在 `application.properties` 里加上 `spring.jpa.open-in-view=false` 即可。

## 后续开发计划

后续可以按以下顺序扩展项目：

### 已完成

1. 增加 `application.properties`，学习应用配置和端口修改。
2. 将控制器整理到 `controller` 包中（另外还拆出了 `service`、`repository`、`entity` 三层）。
3. 增加 `User` 数据模型和 `/users` 接口。
4. 学习 POST、PUT、DELETE 请求。
5. 增加 Service 层，分离控制器和业务逻辑。
6. 增加 H2 数据库和 Spring Data JPA。
7. 实现用户数据的增删改查（CRUD）。

### 下一步

1. 添加 `MockMvc` 接口测试（目前 `src/test/` 还是空的）。
2. 增加 DTO 和参数校验（`@Valid`），让非法请求返回 400 而不是 500。
3. 增加统一异常处理（`@ControllerAdvice`），统一错误响应格式。
4. 增加分页和排序（`Pageable`），以及按姓名、邮箱的模糊查询。
5. 练习实体之间的关联，例如给用户加上「所属部门」。
6. 尝试把 H2 换成 MySQL 或 PostgreSQL —— 主要工作是改 `application.properties` 和驱动依赖。

## Git 信息

当前开发分支：

```text
chapter4
```

本章（chapter4）新增内容：接入 H2 内存数据库与 Spring Data JPA，代码拆成 `controller` /
`service` / `repository` / `entity` 四层，`/users` 从写死返回字符串的演示接口换成真实读写数据库的 CRUD。
想看这一章相对上一章改了什么，执行 `git diff chapter3 chapter4`。

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
