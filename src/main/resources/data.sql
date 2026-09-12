-- 应用启动时预置的示例数据，这样一打开就能看到内容。
--
-- 这个脚本会在 Hibernate 建完表之后才执行，
-- 由 application.properties 里的 spring.jpa.defer-datasource-initialization=true 保证。
-- 少了那一行配置，脚本会先于建表运行，下面的 insert 会直接报"表不存在"。
--
-- 这里不写 id 列，因为主键由 H2 自增生成。

INSERT INTO users (name, email) VALUES ('张三', 'zhangsan@example.com');
INSERT INTO users (name, email) VALUES ('李四', 'lisi@example.com');
INSERT INTO users (name, email) VALUES ('王五', 'wangwu@example.com');
