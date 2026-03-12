# Todo App

A RESTful backend API for managing todos, built with Spring Boot 3 and Java 21. 
Started as example code from a backend development course and extended independently to practice and refine backend fundamentals — including layered architecture, database persistence, authentication, and authorization. 
New features are written independently first, then reviewed by claude-code and refined iteratively.

## Tech Stack

- **Java 21** / **Spring Boot 3**
- **Spring Security** — authentication and role-based / resource-level authorization
- **Spring Data JPA** / **Hibernate** — ORM and database access
- **PostgreSQL** — relational database, run via Docker
- **Maven** — build tool
- **JUnit 5** / **Mockito** — unit testing
- **Springdoc OpenAPI** — API documentation via Swagger UI

## Getting Started

**1. Start the database**
```bash
docker compose up
```

**2. Run the application**
```bash
./mvnw spring-boot:run
```

**3. Explore the API**

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
2. Login via `POST /auth/token` with `{ "username": "...", "password": "..." }` — receive an `accessToken`
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

## Known Limitations

- **Token is not a JWT** — the current access token is the user's raw UUID. JWT implementation is planned.
- **Password update not implemented** — the `PUT /users/{id}` endpoint updates username and role but not password.
- **Schema is recreated on every restart** — `ddl-auto=create` is set for development convenience; this would need to change before any production use.