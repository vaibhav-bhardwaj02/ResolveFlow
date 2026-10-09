# ResolveFlow — Smart Complaint & Service Management Portal

ResolveFlow is a full-stack complaint management system where customers can
raise and track service complaints, support agents resolve assigned
complaints, and administrators manage users, agents, complaint assignment,
and view reports — all secured with JWT-based role authentication.

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.5.5
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL 8
- Maven

**Frontend**
- Plain HTML, CSS, JavaScript (no framework/build tools)
- Communicates with the backend via REST (`fetch` API)

## Project Structure

ResolveFlow/
├── resolveflow/ # Spring Boot backend (REST API)
│ └── src/main/java/com/resolveflow/
│ ├── config/ # Security & CORS configuration
│ ├── controller/ # REST controllers
│ ├── dto/ # Request/response DTOs
│ ├── entity/ # JPA entities
│ ├── enums/ # Role, ComplaintStatus, etc.
│ ├── exception/ # Global exception handling
│ ├── mapper/ # Entity <-> DTO mappers
│ ├── repository/ # Spring Data repositories
│ ├── security/ # JWT filter, UserDetails
│ ├── service/ # Business logic
│ └── util/ # JwtUtil
└── resolveflow-frontend/ # Plain HTML/CSS/JS client


## Core Features

- **Authentication** — Register, login (JWT), forgot/reset password, email verification
- **Role-based access** — `CUSTOMER`, `SUPPORT_AGENT`, `ADMIN`, enforced via Spring Security
- **Complaints** — Raise, update, search, status tracking, ownership-restricted access
- **Admin dashboard** — User management, agent creation, complaint assignment, status reports
- **Notifications** — Triggered on complaint assignment

## Architecture Note

A single `User` entity with a `Role` enum is used instead of separate
Customer/Admin/Agent tables — simplifies relationships (e.g. `Complaint`
references `User` for both `customer` and `assignedAgent`) while keeping
role-based behavior fully enforced at the API layer.

## Running Locally

**Backend**
```bash
cd resolveflow
# configure src/main/resources/application.properties with your MySQL credentials
mvn spring-boot:run
```
Runs on `http://localhost:8080`.

**Frontend**
```bash
cd resolveflow-frontend
# open with VS Code Live Server, or any static file server
```

## Branch Strategy

main
└── develope
├── feature-user-management (auth, security, user mgmt, admin)
├── feature-complaint-management (complaints, categories)
└── feature-admin-dashboard (merged into feature-user-management)

## Status

Backend and frontend complete. Core flows tested end-to-end (auth, JWT,
role-based access, complaint lifecycle, admin operations).