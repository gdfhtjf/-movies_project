# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

在线电影购票系统 — a full-stack movie ticket booking system with a **Spring Boot 4.0 backend** (`backend/`) and a **Vue 3 frontend** (`frontend/`). The two projects run separately and communicate via REST APIs proxied through Vite.

## Commands

### Backend

```bash
cd backend
mvn compile              # Compile
mvn spring-boot:run      # Run on port 8080 (requires MySQL movie_db)
mvn test                 # Run tests (requires MySQL on localhost:3306)
mvn test -Dtest=Javaweb0001ApplicationTests  # Run single test class
```

### Frontend

```bash
cd frontend
npm install              # Install dependencies (first time)
npm run dev              # Dev server on port 5173, proxies /api and /uploads to localhost:8080
npm run build            # Production build
npm run lint             # ESLint
npm run format           # Prettier
```

### Quick start (both)

```bash
# Terminal 1: Start backend
cd backend && mvn spring-boot:run

# Terminal 2: Start frontend
cd frontend && npm run dev
```

Or run `start.bat` from the project root.

Then open `http://localhost:5173`. Admin login: `Admin` / `123456` (auto-created on first backend startup).

## Architecture

### Database (MySQL `movie_db`)

Four core tables: `movies`, `users`, `screenings`, `orders`. Plus Spring Session tables.

- **users** — `student_id` column stores BCrypt-hashed password. Role is `admin` or `user`.
- **movies** — has `poster_path` (uploaded file or auto-downloaded from picsum) and `trailer_path`.
- **screenings** — linked to movies via `movie_id`. Uses `version` column for optimistic locking. Has `remaining_seats` / `total_seats`.
- **orders** — linked to user, movie, and screening. `seat_number` stores comma-separated seat labels. Uses `version` for optimistic locking. Status: `paid` → `cancelled`.

### Backend Layering

```
controller → service (interface → impl) → mapper (MyBatis-Plus BaseMapper)
```

All services extend `ServiceImpl<Mapper, Entity>`, providing CRUD via `lambdaQuery()` / `lambdaUpdate()` without writing SQL. Custom business logic in impl classes.

Key patterns:
- **Result<T>** (`com.movie.common.Result`) — unified response wrapper `{code, message, data}`. Static factories: `Result.success(data)`, `Result.error(code, msg)`.
- Frontend axios interceptor unwraps `response.data` so Vue code receives `Result` directly.
- **Optimistic locking**: `OrderServiceImpl` uses `lambdaUpdate().eq(version).set(version+1)` to prevent race conditions on seat booking.
- **Cascading deletes**: `MovieServiceImpl.deleteMovieWithCheck()` deletes associated screenings then orders.

### Security Layers (executed in order)

1. **Filters** (Servlet Filter chain, configured in `ServletComponentConfig`):
   - `RequestLogFilter` (1) — logs method, URI, elapsed ms
   - `CharEncodingFilter` (2) — UTF-8
   - `CorsFilter` (3) — sets `Access-Control-Allow-Origin: *`
   - `LoginCheckFilter` (4) — redirects unauthenticated users from `/jsp/*`, `/admin/*`
   - `SensitiveWordFilter` (5) — replaces English profanity with `*` in non-GET params
   - `XssProtectionFilter` (6) — escapes `<>&"'` in params
   - `RateLimitFilter` (7) — 10000 req/min per IP

2. **Interceptors** (Spring MVC, configured in `WebMvcConfig`):
   - `LoginInterceptor` — blocks `/api/orders/**`, `/api/auth/logout`, `/api/users/*/password`
   - `AdminInterceptor` — allows public GET for `/api/movies`, `/api/screenings`, `/api/posters`, `/api/orders/screening`; other methods on admin paths require `admin` role

3. **AOP**: `OperationLogAspect` logs all CUD operations with username, IP, args count, elapsed ms.

### Frontend Routing & Layouts

`frontend/src/layouts/`:
- `UserLayout.vue` — public pages (home, movie list, detail, login, register) + authenticated (seat selection, orders)
- `AdminLayout.vue` — admin pages, requires auth + admin role (router guard)

Auth flow: `useAuthStore.fetchMe()` calls `GET /api/auth/me`. Session maintained via cookie (Spring Session JDBC).

### File Uploads

Uploaded posters/trailers go to `backend/movie_uploads/` (configured via `file.upload-dir` in application.yml). Served via Spring resource handler at `/uploads/**`. `PosterDownloader` auto-downloads placeholder posters from picsum.photos on startup.

### Data Initialization (CommandLineRunner beans)

- `DataInitializer` — always runs: creates `Admin` user, upgrades plaintext passwords to BCrypt
- `MovieImporter` — `SHOULD_IMPORT = false` (15 Chinese movies)
- `ScreeningDataGenerator` — `SHOULD_GENERATE = false`
- `DataCleaner` — `CLEAN_DUPLICATES = false`
