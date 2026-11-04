-- 测试专用的数据，由 getMemberById 上的 @Sql 在执行测试之前加载。
--
-- 这里显式写了 id。平时新增记录不用管 id（交给数据库自增），
-- 但测试里要拿一个确定的 id 去查，就不能靠自增算出来是多少了。

INSERT INTO members (id, name, email) VALUES (100, '唐僧', 'tangseng@example.com');
