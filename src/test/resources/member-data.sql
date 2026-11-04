-- 测试专用的数据，由测试方法上的 @Sql 在执行测试之前加载。
-- 放在 src/test/resources 下，只在跑测试时才会被用到，不会影响应用本身。

INSERT INTO members (name, email) VALUES ('孙悟空', 'sunwukong@example.com');
INSERT INTO members (name, email) VALUES ('猪八戒', 'zhubajie@example.com');
