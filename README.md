# Todo App

A full-stack todo application with a Spring Boot 3 / Java 21 REST API backend and a Vue 3 frontend.
Started as example code from a backend development course and extended independently to practice and refine full-stack fundamentals — including layered architecture, database persistence, authentication, authorization, and frontend-backend integration.
New features are written independently first, then reviewed by claude-code and refined iteratively.

## Tech Stack

### Backend
- **Java 21** / **Spring Boot 3**
- **Spring Security** — authentication and role-based / resource-level authorization
- **Spring Data JPA** / **Hibernate** — ORM and database access
- **PostgreSQL** — relational database, run via Docker
- **Maven** — build tool
- **JUnit 5** / **Mockito** — unit testing
- **Springdoc OpenAPI** — API documentation via Swagger UI

### Frontend
- **Vue 3** — component-based UI framework (Composition API, `<script setup>`)
- **TypeScript** — static typing
- **Vite** — build tool and dev server
- **PrimeVue** — UI component library (Aura theme)
- **Tailwind CSS** — utility-first CSS framework
- **Axios** — HTTP client for API calls
- **Zod** — schema validation for forms

## Getting Started

**1. Start the database**
```bash
docker compose up
```

**2. Run the backend**
```bash
cd backend
./mvnw spring-boot:run
```

**3. Run the frontend**
```bash
cd frontend
npm install
npm run dev
```

**4. Explore the API**

Swagger UI: http://localhost:8080/swagger.html

## API Overview

### Authentication
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| `POST` | `/users` | Public | Register a new user |
| `POST` | `/auth/token` | Public | Login — returns an access token |

### Todos
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| `GET` | `/todos` | Authenticated | Get all todos owned by the current user |
| `GET` | `/todos/{id}` | Owner or Admin | Get a single todo |
| `POST` | `/todos` | Authenticated | Create a new todo |
| `PUT` | `/todos/{id}` | Owner or Admin | Update a todo |
| `DELETE` | `/todos/{id}` | Owner or Admin | Delete a todo |

### Users
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| `GET` | `/users` | Admin only | List all users |
| `GET` | `/users/{id}` | Self or Admin | Get a user |
| `PUT` | `/users/{id}` | Self or Admin | Update a user |
| `DELETE` | `/users/{id}` | Admin only | Delete a user |

**Auth flow:**
1. Register via `POST /users`
2. Login via `POST /auth/token` with `{ "username": "...", "password": "..." }` — receive an `accessToken` (JWT)
3. Pass the token in subsequent requests: `Authorization: Bearer <accessToken>`

## Architecture

3-layer architecture with clear separation of concerns:

```
Controller  →  Service  →  Repository
   (HTTP)     (Logic)      (Database)
```

- **Controller** (`controller/`) — HTTP routing and request/response mapping. One controller per entity.
- **Service** (`service/`) — All business logic. Controllers delegate here.
- **Repository** (`repository/`) — JPA interfaces backed by Hibernate/PostgreSQL.
- **DTOs** (`dto/`) — Separate request/response objects used at API boundaries, keeping JPA entities out of the HTTP layer.
- **Security** (`security/`) — Spring Security configuration, token filter, and principal wrappers.

## Running Tests

```bash
./mvnw test
```

## Known Limitations / What's next

- **Schema is recreated on every restart** — `ddl-auto=create` is set for development convenience; this would need to change before any production use.
- **Frontend is registration-only** — login, todo management, and routing are not yet implemented.
- **No shared Axios instance** — services import `axios` directly; a shared instance with a request interceptor (to attach the JWT automatically) and a response interceptor (to handle 401s globally) should be extracted to `src/services/api.ts`.