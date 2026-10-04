# smartgn-iam-service

Identity and Access Management microservice for SmartGN.
Spring Boot 3.5, Java 21, PostgreSQL (Database per Service: `iam_db`, schema `iam`).

## Local development (PowerShell)

```powershell
docker compose up -d
$env:SPRING_PROFILES_ACTIVE = "local"
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

With the `local` profile the password-recovery link is printed to the application console.

## Environment variables (Render)

| Variable | Description |
|---|---|
| `DB_URL` | `jdbc:postgresql://<internal-host>:5432/<db>` (JDBC format) |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | Signing key, at least 32 characters |
| `JWT_EXPIRATION_MINUTES` | Optional, defaults to 15 |
| `PASSWORD_RESET_LINK_BASE_URL` | Frontend URL for the recovery link (`?token=...` is appended) |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Optional, defaults to 30 |

Render sets `PORT` automatically. Health check path: `/actuator/health`.

## Endpoints

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/v1/auth/sign-up` | Register an account (PROPIETARIO or ADMINISTRADOR) | Public |
| POST | `/api/v1/auth/sign-in` | Authenticate and get a JWT | Public |
| POST | `/api/v1/auth/forgot-password` | Request a password reset link | Public |
| POST | `/api/v1/auth/reset-password` | Set a new password with the recovery token | Public |
| POST | `/api/v1/profiles` | Create my profile | Authenticated |
| GET | `/api/v1/profiles/me` | Get my profile | Authenticated |
| GET | `/api/v1/profiles/{profileId}` | Get a profile (owner or SUPERADMIN) | Authenticated |
| PUT | `/api/v1/profiles/{profileId}` | Update a profile (owner only) | Authenticated |
| GET | `/api/v1/subscriptions/me` | Get my plan (FREE or PRO) and enabled features | Authenticated |

Authenticated endpoints expect `Authorization: Bearer <token>`.
The JWT carries the claims `accountId`, `email`, `role` and `plan`.

## Project structure

Hexagonal architecture with one package per bounded context under `com.smartgn.iam`:
`auth`, `profile` and `subscription`, each with `domain`, `application` (use cases and ports),
`infrastructure` (adapters) and `interfaces/rest`. Cross-cutting configuration lives in `shared`.

The password-recovery email is delivered through the `PasswordResetNotifier` port. Outside the
`local` profile a placeholder adapter is active until the external email system is connected.
