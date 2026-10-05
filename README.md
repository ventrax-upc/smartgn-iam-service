# SmartGN IAM Service

SmartGN service for registering accounts, signing in, managing user profiles, and checking subscription plans. It handles user authentication for other SmartGN services.

Property, support, installation, and device management is handled by the [Management service](https://github.com/ventrax-upc/smartgn-management-service).

## Architecture

Hexagonal architecture (ports and adapters), organized into authentication, profile, and subscription modules. Each module separates domain models, application use cases, REST interfaces, and infrastructure adapters. The service owns its PostgreSQL database and issues authentication tokens for other SmartGN services.

## Technologies

- Java 21 and Spring Boot 3.5.6.
- Spring Web MVC for REST APIs and Spring Security with JWT authentication.
- PostgreSQL, Spring Data JPA/Hibernate, and Flyway for persistence and database migrations.
- Maven, Docker, and OpenAPI/Swagger through springdoc.

## Project structure

```text
src/main/java/com/smartgn/iam/  Application code, grouped by feature
  auth/                       Account registration and authentication
  profile/                    User profiles
  subscription/               Subscription plans and features
  shared/                     Shared configuration and utilities
src/main/resources/           Configuration and database migrations
src/test/                     Automated tests
pom.xml                       Project dependencies and build settings
docker-compose.yml            Local service configuration
Dockerfile                    Application container definition
```
