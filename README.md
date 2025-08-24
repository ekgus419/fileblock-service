# 📌 FileBlock Service

파일 확장자 차단 기능을 제공하는 **백엔드(Spring Boot, PostgreSQL)** + **프론트엔드(React, Vite)** 통합 프로젝트입니다. 보안상 위험할 수 있는 확장자를 관리하고, 파일 업로드 시 자동으로 차단 여부를 검사합니다.

---

## 🌐 배포 URL (Render)

- **사이트(프론트+백엔드 단일 URL)**: https://fileblock-service.onrender.com/

---

## 🛠 기술 스택

### Backend

- **Java 24** (Gradle Toolchain)
- **Spring Boot 3.5.4**
    - spring-boot-starter-web
    - spring-boot-starter-validation
    - spring-boot-starter-data-jpa
- **PostgreSQL 15** (runtimeOnly `org.postgresql:postgresql`)
- **Lombok** (compileOnly + annotationProcessor)
- **Test**: spring-boot-starter-test, JUnit Platform
- **Build/Plugins**
    - Gradle Plugins: `org.springframework.boot 3.5.4`, `io.spring.dependency-management 1.1.7`, `java`

### Frontend

- **React 19.1.1**, **React DOM 19.1.1**
- **React Router DOM 7.8.1**
- **Vite 7.1.2**
- **Tailwind CSS 4.1.12** (+ `@tailwindcss/postcss`)
- **Axios 1.11.0**
- **ESLint 9.x** (`@eslint/js`, `eslint-plugin-react-hooks`, `eslint-plugin-react-refresh`) + `globals`

### Infra & Packaging

- Dockerfile (프론트 정적 빌드 + 백엔드 JAR 통합 런타임)
- Docker Compose (개발용, PostgreSQL + 앱)
- Render 배포(컨테이너 기반)

---

## 📁 프로젝트 구조 (요약)

```
fileblock-service/
├─ fileblock-backend/                  # Spring Boot 앱
│  ├─ src/main/java/com/example/fileblock/
│  │  ├─ extension/                    # 고정/커스텀 확장자 API/Service/Repository
│  │  ├─ file/                         # 파일 업로드 API(차단 검사)
│  │  └─ global/                       # 공통 응답/예외/CORS 등
│  ├─ src/main/resources/
│  │  ├─ application.yml               # DB/JPA 설정
│  │  └─ data.sql                      # 기본 고정 확장자 시드
│  ├─ build.gradle                     # Java 24 / Boot 3.5.4
│  └─ docker-compose.yml               # dev용 Postgres+app
├─ fileblock-frontend/                 # React + Vite 프론트엔드
│  ├─ src/
│  │  ├─ api/extensionApi.js           # axios 래퍼
│  │  └─ pages/ExtensionManager.jsx    # 메인 화면
│  └─ package.json                     # React 19, Vite 7
├─ Dockerfile                          # 단일 컨테이너 빌드(프론트+백엔드)
└─ render.yaml                         # Render 배포 설정
```

---

## 🚀 실행 방법

### 1) 배포 사이트 사용

- https://fileblock-service.onrender.com/
    - 루트 경로에서 프론트 정적 파일이 서빙되며, 동일 도메인으로 백엔드 API 호출

### 2) 로컬 개발

### 2-1. Docker Compose (DB+백엔드)

```bash
cd fileblock-backend
docker-compose up --build
```

- API: [http://localhost:8080](http://localhost:8080/)
- DB: localhost:5432 (`postgres`/`postgres`)

### 2-2. 프론트엔드 개발 서버(Vite)

```bash
cd ../fileblock-frontend
npm install
npm run dev
```

- 프론트: [http://localhost:5173](http://localhost:5173/)
- CORS: 백엔드에서 `localhost:5173` 허용되어 있어야 합니다.

### 3) Docker 이미지 (프론트+백엔드)

```bash
cd fileblock-service
docker build -t fileblock-service:latest .

docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<DB_HOST>:5432/fileblock" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  fileblock-service:latest
```

---

## 📚 주요 기능 & API 개요

### 1) 고정 확장자 (Fixed Extensions)

- **설명**: 미리 정의된 위험 확장자 목록, 각 항목에 대해 차단 여부(`isBlocked`)를 토글
- **영속화**: 토글 상태는 DB에 저장 → 새로고침/재접속 시 유지
- **예시 API**
    - `GET /extensions/fixed` — 목록 조회
    - `PUT /extensions/fixed/{seq}?isBlocked=true|false` — 차단 토글

### 2) 커스텀 확장자 (Custom Extensions)

- **설명**: 사용자가 직접 관리하는 차단 목록
- **제약**: 최대 길이 **20자**, 최대 개수 **200개**, **중복 불가**
- **예시 API**
    - `GET /extensions/custom` — 목록 조회
    - `POST /extensions/custom?extension=xxx` — 추가 (정규화/중복 검증)
    - `DELETE /extensions/custom/{seq}` — 삭제

### 3) 파일 업로드 차단 검사

- 업로드 파일명에서 확장자 추출 → 고정/커스텀 차단 목록과 비교
- 차단 대상이면 업로드 거부, 표준 응답 포맷 반환
- **예시 API**
    - `POST /files/upload` — 멀티파트 업로드(차단 여부 판정)

### 공통 응답

```json
{
  "success": true,
  "data": { ... },
  "error": null
}

```

에러 시 `success=false`, `error={ code, message }` 형식으로 반환

---

## ⚙️ 설계 의도 및 고려사항

1. **레이어드 아키텍처**: Controller → Service → Repository로 분리하여 응집도와 테스트 용이성 확보
2. **입력 정규화**: 확장자 입력 시 소문자화/마침표 제거/trim → 중복/오입력 최소화
3. **제약 강제**: DB Unique, 길이·개수 검증으로 무결성 보장
4. **예외/응답 일관성**: 전역 예외 핸들러 + 표준 응답(`ApiResponse`) 채택
5. **배포 단순화**: 단일 컨테이너에서 프론트 정적 파일과 API 동시 제공 (단일 URL)
6. **보안 고려**: MIME 체크(기본 수준) 및 확장 가능 구조. 운영에서는 파일 시그니처 검사 도입 권장

---

## 🔍 트러블슈팅

- **중복 키 충돌**: 커스텀 확장자 unique 제약으로 `duplicate key` 발생 가능 → 입력 정규화 + 서비스 레벨 중복 검사로 선제 방지, 에러 메시지 명확화
- **CORS**: 개발 포트(5173) 허용, 배포는 동일 도메인으로 호출하여 CORS 불필요
- **DB 초기 데이터**: `data.sql` 로 고정 확장자 시드 → 운영 전환 시 마이그레이션 도구(Flyway) 고려
- **고정 확장자 토글 시 500 에러**: DB 연결/마이그레이션 상태 확인, 중복 시드 여부 점검
- **커스텀 확장자 추가 시 에러**: 공백/마침표 포함/대소문자 이슈 → 정규화 규칙 확인, 20자/200개 한도 검증
- **프론트에서 API 호출 실패**: 배포 환경에서는 동일 도메인 사용, 개발 환경은 `5173 → 8080` CORS 허용 확인

---
