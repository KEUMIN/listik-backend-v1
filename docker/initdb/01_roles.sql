-- 서비스 로그인 계정
CREATE ROLE user_service LOGIN PASSWORD 'user123!';
CREATE ROLE book_service LOGIN PASSWORD 'book123!';

-- 각 계정의 기본 search_path를 자신의 스키마로
ALTER ROLE user_service SET search_path TO "user", public;
ALTER ROLE book_service SET search_path TO "book", public;
