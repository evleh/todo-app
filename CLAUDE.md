# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## My Learning Preferences

I am a junior developer focused on deep understanding, not just working code.

- Always explain *why* you chose an approach, not just *what* the code does
- Point out trade-offs and alternatives when they exist
- If I write code first and ask for a review, critique it before rewriting it
- Flag anything in your output that I should research and understand more deeply
- Don't just fix bugs — explain what caused them and how to avoid them in future
- Keep explanations clear but don't oversimplify; I want to actually learn
- Label concepts and techniques by seniority level using these exact tags:
  - [JUNIOR] — foundational knowledge every junior developer must know
  - [MID] — patterns and practices expected at mid-level
  - [SENIOR] — advanced approaches not yet expected of me, but good to be aware of

## Commands

**Start the database (required before running the app):**
```bash
docker compose up
```

**Build and run:**
```bash
./mvnw spring-boot:run
```

**Run all tests:**
```bash
./mvnw test
```

**Run a single test class:**
```bash
./mvnw test -Dtest=TodoAppApplicationTests
```

**Build without running tests:**
```bash
./mvnw package -DskipTests
```

**Swagger UI:** http://localhost:8080/swagger.html
**OpenAPI JSON:** http://localhost:8080/api

## Architecture

Spring Boot 3 / Java 21 REST API with a 3-layer architecture:

1. **Controller** (`controller/`) — HTTP routing, request/response mapping. One controller per entity.
2. **Service** (`service/`) — Business logic. Controllers delegate all logic here.
3. **Repository** (`repository/`) — JPA interfaces extending `JpaRepository`, backed by Hibernate/PostgreSQL.

**Entities** (`entity/`): `Todo` (the main resource), `MyUser` (has UUID string ID, stores BCrypt-hashed password, defaults role to `ROLE_USER`), `Role` (enum).

**DTOs** (`dto/`): `TodoCreateRequest`, `TodoUpdateRequest`, `TodoResponse`, `UserCreateRequest`, `UserUpdateRequest`, `UserResponse`, `TokenRequest`, `TokenResponse` — all implemented as Java records. Used at API boundaries to decouple from JPA entities.

**Security** (`security/`):
- `SecurityConfig` — defines the `SecurityFilterChain`. Public routes: `POST /users`, `/auth/token/**`, `/swagger-ui/**`, `/swagger.html`, `/api/**`. All other routes require authentication. Also configures `DaoAuthenticationProvider` with `BCryptPasswordEncoder` and `CustomUserDetailsService`.
- `TokenAuthenticationFilter` — `OncePerRequestFilter` that reads the `Authorization: Bearer <token>` header, validates the JWT via `JwtService`, and sets the `SecurityContext`. If the token is missing or invalid, the filter passes through and Spring Security rejects the request at the authorization step.
- `JwtService` — issues and validates JWTs signed with HMAC-SHA using a secret key from `application.properties`. Token payload contains: `sub` (userId), `username`, `roles`, `iat`, `exp`. Uses `io.jsonwebtoken` (JJWT) library.
- `CustomUserDetailsService` — loads `UserPrincipal` from the database by username for `DaoAuthenticationProvider`.
- `UserPrincipal` / `UserPrincipalAuthenticationToken` — Spring Security principal wrappers.

**Auth flow:**
1. Register: `POST /users` (public)
2. Login: `POST /auth/token` with `{username, password}` → returns `{accessToken}` (a signed JWT)
3. Authenticated requests: pass `Authorization: Bearer <accessToken>` header
4. On each request: `TokenAuthenticationFilter` validates the JWT signature and expiry — no database lookup required

**Authorization:**
- `ROLE_USER` — can only read/update/delete their own todos
- `ROLE_ADMIN` — can read all todos regardless of ownership
- Resource-level ownership is enforced in `TodoService`, not at the filter level

**Database:** PostgreSQL via Docker. Credentials in `application.properties`: user `eww`, password `eww`, db `todoapp`, port `5432`. `spring.jpa.hibernate.ddl-auto=create` (drops and recreates schema on every start — change to `none` for production).

**Exception mapping:** Custom exceptions in `exception/` use `@ResponseStatus` to map to HTTP status codes (e.g. `EntityNotFoundException` → 404, `PermissionDeniedException` → 403).

**Tests** (`src/test/`):
- `TodoServiceTests` — unit tests using Mockito. Covers `readAll` with role-based and ownership logic.
- `UserServiceTests` — unit tests using Mockito. Covers create, read, readAll, update, delete.
- Integration tests for the auth flow (`AuthControllerTest`) — in progress.


