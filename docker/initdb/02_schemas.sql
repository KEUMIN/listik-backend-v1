-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS "user";
CREATE SCHEMA IF NOT EXISTS "book";

-- 스키마 소유권 이관
ALTER SCHEMA "user" OWNER TO user_service;
ALTER SCHEMA "book" OWNER TO book_service;

-- 명시적 권한(소유자라 기본 충분하지만 가독성 위해 부여)
GRANT USAGE ON SCHEMA "user" TO user_service;
GRANT USAGE ON SCHEMA "book" TO book_service;
