-- 앱이 시작할 때 넣는 샘플 데이터입니다.
-- Hibernate가 article 테이블을 만든 뒤에 실행됩니다.
-- (application.properties 의 spring.jpa.defer-datasource-initialization=true)

INSERT INTO article (title, content) VALUES ('첫 번째 글', 'data.sql 로 넣은 첫 번째 글입니다.');
INSERT INTO article (title, content) VALUES ('두 번째 글', 'Thymeleaf 목록 페이지에서 보이는 두 번째 글입니다.');
INSERT INTO article (title, content) VALUES ('세 번째 글', '보러가기를 누르면 상세 페이지로 이동합니다.');
