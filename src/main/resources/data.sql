-- 应用启动时预置的示例数据，这样一打开就能看到内容。
--
-- 这个脚本会在 Hibernate 建完表之后才执行，
-- 由 application.properties 里的 spring.jpa.defer-datasource-initialization=true 保证。
-- 少了那一行配置，脚本会先于建表运行，下面的 insert 会直接报"表不存在"。
--
-- 注意：data.sql 是直接执行的 SQL，不经过实体类的 @PrePersist，
-- 所以 articles 的 created_at / updated_at 这两个非空列必须自己写死，
-- 漏了会直接报"NULL not allowed for column"。

-- 站内成员（有权限写文章的人）。
INSERT INTO members (name, email) VALUES ('张三', 'zhangsan@example.com');
INSERT INTO members (name, email) VALUES ('李四', 'lisi@example.com');
INSERT INTO members (name, email) VALUES ('王五', 'wangwu@example.com');

-- 三篇已发布文章 + 一篇草稿（published = false），用来演示列表过滤。
INSERT INTO articles (title, content, author, tags, published, created_at, updated_at)
VALUES ('用 Spring Boot 搭一个博客后端：从建表到接口',
        '这个项目最初只是一个练习 HTTP 映射的 Spring Boot 示例，后来被改造成博客系统后端。改造顺序是：先加实体和 Repository，再补 Service 里的业务规则，最后把 Controller 的接口补齐。每一步都能单独启动验证，不用等全部写完才知道对不对。',
        '张三', 'Spring Boot,JPA,后端', true, '2026-10-20 09:30:00', '2026-10-20 09:30:00');

INSERT INTO articles (title, content, author, tags, published, created_at, updated_at)
VALUES ('JPA 派生查询的命名规则，一次记清',
        'findBy 后面跟字段名，Containing 变成 like %关键字%，IgnoreCase 忽略大小写，OrderBy 接字段名和方向，关联对象的属性用下划线隔开。这套命名规则看着长，但写顺了之后一个接口的查询往往一行就能表达，不用碰 SQL。',
        '李四', 'JPA,数据库', true, '2026-10-25 14:10:00', '2026-10-25 14:10:00');

INSERT INTO articles (title, content, author, tags, published, created_at, updated_at)
VALUES ('分页接口的页码为什么从 0 开始',
        'Spring Data 的 PageRequest 用的是从 0 开始的页码，而前端界面上写的通常是第 1 页。这中间差了一位，是接前后端时最容易踩的坑之一。这个项目在文档里把页码规则写清楚，接口参数默认值也直接给 0，避免两边靠猜。',
        '张三', 'REST,分页', true, '2026-10-28 20:05:00', '2026-10-28 20:05:00');

INSERT INTO articles (title, content, author, tags, published, created_at, updated_at)
VALUES ('草稿：给博客加评论审核（还没写完）',
        '思路是给 comments 表加一个 status 字段，新评论默认待审核，后台通过之后才出现在前台列表里。还没来得及动手，先把想法记在这里。',
        '王五', '草稿,规划', false, '2026-11-01 10:00:00', '2026-11-01 10:00:00');

-- 评论挂在文章下面，article_id 对应上面几篇文章的自增 id（从 1 开始，按插入顺序）。
INSERT INTO comments (article_id, author, content, created_at)
VALUES (1, '李四', '这个改造顺序很实用，先能跑起来再补细节，比一次写完再调好受多了。', '2026-10-20 11:00:00');

INSERT INTO comments (article_id, author, content, created_at)
VALUES (1, '王五', '请问换成 MySQL 需要改哪些地方？', '2026-10-20 15:30:00');

INSERT INTO comments (article_id, author, content, created_at)
VALUES (2, '张三', '下划线那个坑我踩过，写成 findByArticleId 启动直接报错。', '2026-10-26 09:15:00');

INSERT INTO comments (article_id, author, content, created_at)
VALUES (3, '李四', '前端那边确实得记着减一，不然第一页永远看不到最新文章。', '2026-10-29 08:40:00');
