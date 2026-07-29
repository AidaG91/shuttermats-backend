# ShutterMats — Backend

REST API for **ShutterMats**, a booking platform for a combat-sports (BJJ / grappling) event photographer. Athletes browse upcoming tournaments and request photo coverage through a public form; the photographer manages every request and the event calendar from a protected admin panel.

Live companion repo: [shuttermats-frontend](https://github.com/AidaG91/shuttermats-frontend)

## Overview

The domain is simpler than a typical multi-tenant SaaS on purpose: there is **one admin** (the photographer) and **no athlete accounts**. Athletes submit a coverage request as a public form — name, contact info, championship, category, coverage preferences, billing details — and get an immediate on-screen confirmation. The admin authenticates with JWT and, from the dashboard, reviews requests, updates their status, and runs full CRUD on events, including event cover images.

## Tech Stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation)
- PostgreSQL (runtime) / H2 (tests only)
- Spring Security with a stateless, custom JWT filter chain (`jjwt`)
- Maven
- JUnit 5, Mockito, Spring Boot Test (unit + integration tests)

## Key Features

- Public, paginated event listing with filters by status and location (`GET /api/events`)
- Coverage request submission with a rich, nested payload (athlete, championship, category, location, coverage preferences, billing, confirmations) and configurable paid extras
- Admin JWT authentication against a single configured admin user (no `Athlete`/`Admin` DB tables — see `app.admin.*` properties)
- Admin dashboard endpoints to list/filter coverage requests by status and event, view a request in detail, and update its status with an optional response message
- Admin CRUD on events, with `multipart/form-data` image upload for the event cover photo (served back as a static resource)
- Centralized error handling (`@RestControllerAdvice`) returning a consistent JSON error shape, including field-level validation errors
- Seed data (`data.sql`) with real events from the Catalan grappling scene and the configured coverage extras, safe to re-run thanks to `ON CONFLICT` upserts

## Project Structure

```
src/main/java/ShutterMats/Backend/
├── config/          # Security (JWT filter chain, CORS) and static resource config
├── controller/       # REST controllers (public + /admin)
├── dto/               # Request/response DTOs (records), including nested coverage-request DTOs
├── entity/            # JPA entities: Event, CoverageRequest, CoverageExtra + enums
├── exception/         # Custom exceptions + global exception handler
├── mapper/            # Entity <-> DTO mapping
├── repository/        # Spring Data repositories + JPA Specifications for filtering
├── security/          # Admin UserDetailsService, AuthenticationManager, JWT login/authorization filters
└── service/           # Business logic
```

## API Reference

### Public

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/events` | Paginated event list, filterable by `status` and `location` |
| GET | `/api/events/locations` | Distinct list of event locations (for filters) |
| GET | `/api/events/{id}` | Event detail |
| GET | `/api/extras` | Active coverage extras (with price) |
| POST | `/api/requests` | Submit a coverage request |

### Admin (JWT required, `ROLE_ADMIN`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/login` | Admin login, returns a JWT |
| GET | `/api/admin/requests` | Paginated coverage requests, filterable by `status` and `eventId` |
| GET | `/api/admin/requests/{id}` | Coverage request detail |
| PATCH | `/api/admin/requests/{id}/status` | Update request status and optional admin response |
| POST | `/api/admin/events` | Create event (`multipart/form-data`, optional image) |
| PUT | `/api/admin/events/{id}` | Update event (`multipart/form-data`, optional image) |
| DELETE | `/api/admin/events/{id}` | Delete event |

## Getting Started

### Prerequisites

- Java 25
- Maven (or use the bundled `./mvnw`)
- A running PostgreSQL instance

### 1. Configure the database

Create a database (defaults to `shuttermatsdb`) and either export the matching environment variables or rely on the defaults in `application.properties`:

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=shuttermatsdb
DB_USER=postgres
DB_PASSWORD=your_password
```

### 2. Configure the admin user and JWT secret

There is no admin table — credentials are read from configuration. Override these outside local dev:

```
ADMIN_USERNAME=admin
ADMIN_PASSWORD_HASH=<bcrypt hash>
JWT_SECRET=<a long random secret>
JWT_EXPIRATION_MS=86400000
```

### 3. Run the application

```
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. On first boot, Hibernate creates the schema and `data.sql` seeds the initial events and coverage extras (idempotent thanks to `ON CONFLICT`).

### 4. Run the tests

```
./mvnw test
```

Tests run against an in-memory H2 database and cover mappers, services, security components, and controller integration flows.

## Configuration Reference

All configurable values live in `src/main/resources/application.properties` and can be overridden via environment variables: server port, datasource credentials, CORS allowed origins (`app.cors.allowed-origins`, defaults to the Vite dev server at `http://localhost:5173`), upload directory and max file size for event images, admin credentials, and JWT secret/expiration.

## Roadmap

- Payment integration for coverage requests
- Automatic pricing calculation (base fee + extras) — extras are already modeled, base pricing is still manual
- Email notifications on status changes
