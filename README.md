# CSLU Rest API
본 저장소는 CSLU의 웹·앱 클라이언트와 연동되는 백엔드 서버입니다.  
Spring Boot 기반의 REST API로, 실시간 문제 데이터 처리, 사용자 인증, 풀이 결과 제공 등 핵심 기능을 수행합니다.
<br>
<br>

## 기술 스택

- **Language**: Java 21
- **Build Tool**: Gradle
- **Framework**: Spring Boot
- **Security**: Spring Security, JWT
- **Database**: MySQL
- **ORM**: JPA (Hibernate)
- **API Docs**: Swagger

<br>

## 개발 기간
- **전체 개발기간** : 2024.02.13 ~ 2024.09.26
- 2024.02 : 프로젝트 기획, 데이터 구조 설정 및 DB 설계
- 2024.03 ~ 2024.05 : API 구현
- 2024.06 ~ 2024.10. : 서버 운영, 클라이언트 연동 지원, 유지보수

<br>


## 주요 기능
- **사용자 인증 및 권한 관리**
  - Google OAuth2 로그인 및 회원가입
  - JWT 기반 인증 및 토큰 재발급

- **문제 데이터 처리**
  - 기본 문제 및 사용자 문제 생성/수정/삭제
  - 문제 카테고리 분류, 이미지 업로드

- **문제 풀이 기능**
  - 사용자 맞춤 문제 제공 (풀이 이력, 지정 과목)
  - 사용자별 풀이 이력 저장 및 통계 조회

- **신고 및 승인 시스템**
  - 문제 신고 접수 및 관리자 검토
  - 문제 승인/반려 처리

- **관리자 기능**
  - 관리자 전용 로그인
  - 미승인 문제 및 신고 목록 관리

- **API 문서화 및 테스트**
  - Swagger 기반 API 명세 자동 생성

- **로깅 및 예외 처리**
  - API 요청/응답 및 예외 로그 기록
  - 클라이언트 연동 오류 추적을 위한 로깅 파일 관리

<br>

## 주요 API 엔드포인트

| 메서드 | 엔드포인트                             | 설명                     |
|--------|----------------------------------------|--------------------------|
| POST   | /api/manager/login                     | 관리자 로그인             |
| POST   | /api/manager/logout                    | 관리자 로그아웃           |
| POST   | /api/user/auth/google/sign-up         | 사용자 회원 가입          |
| POST   | /api/user/auth/google/login           | 사용자 로그인             |
| POST   | /api/quiz/default                      | 기본 문제 생성            |
| POST   | /api/quiz/user                         | 사용자 문제 생성          |
| GET    | /api/quiz/random                       | 랜덤 문제 조회            |
| POST   | /api/quiz/submit                       | 문제 풀이 제출            |

https://docs.google.com/spreadsheets/d/1E_B1Mn7FbFQsqiZuWPjnH5RRe5uFBIAkrUzkHxUUgMs/edit?gid=0#gid=0
