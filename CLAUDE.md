## Commands

**Start the database (required before running the app):**
```bash
docker compose up
```

**Build and run:**
```bash
./mvnw spring-boot:run
```

**Run tests:**
```bash
./mvnw test
./mvnw test -Dtest=TodoAppApplicationTests
./mvnw package -DskipTests
```

**Swagger UI:** http://localhost:8080/swagger.html
**OpenAPI JSON:** http://localhost:8080/api

**Frontend:**
```bash
cd frontend && npm install
cd frontend && npm run dev   # runs at http://localhost:5173
```

## Stack

- **Backend:** Spring Boot 3 / Java 21, PostgreSQL (Docker), Spring Security, JJWT
- **Frontend:** Vue 3 (`<script setup>`), TypeScript, Vite, PrimeVue (Aura theme), Tailwind CSS, Axios, Zod

## Key Gotchas

- `spring.jpa.hibernate.ddl-auto=create` — drops and recreates the schema on every start. Change to `none` before any real use.
- Authorization ownership (ROLE_USER can only access their own todos) is enforced in `TodoService`, not in the security filter.
- Frontend JWT is stored in `localStorage`. Router guard uses `meta.requiresAuth` on protected routes.

## Frontend Status

Registration, login, and logout are fully implemented. Todo management frontend is not yet built.
