# Task Tracking Application – README

## Overview

This Task Tracking Application is a full-stack project built with **Spring Boot**, **Spring Data JPA**, **H2 Database**, and a simple **HTML/CSS/JavaScript frontend**. It allows users to manage projects and tasks with features such as user registration, task assignment, filtering, due‑today checks, audit logging, and more.

The backend exposes a clean RESTful API, documented via Swagger/OpenAPI, and supports optional advanced features such as JWT authentication, task search, email notifications, and audit logging.

---

## Features

### ✔ Core Features

* User registration and retrieval
* Project creation, update, deletion, and listing
* Task creation, filtering, updating, and deletion
* Tasks due today
* View tasks assigned to a user
* Audit logging for CREATE/UPDATE/DELETE actions

### ✔ Frontend Features

* User registration & login page (basic)
* Project list & creation form
* Task list with filtering (status)
* Task creation & edit forms
* Axios/Fetch API integration

### ✔ Optional Advanced Features (Choose at least 2)

* JWT authentication with Spring Security
* Task search with JPA Specifications
* Email notifications using Spring Mail
* File upload/download for task attachments
* Task comments
* Task activity logging (audit log)

---

## Technologies Used

### Backend

* Spring Boot 3.x
* Spring Web
* Spring Data JPA (Hibernate)
* Spring Validation
* H2 Database
* Flyway (schema migrations)
* Swagger / Springdoc OpenAPI
* JUnit 5 & Mockito

### Frontend

* HTML5, CSS3, JavaScript
* Axios or Fetch API

---

## Data Model

### Entities

#### **User**

* id (Long)
* username (String)
* email (String)
* password (String)
* createdAt (LocalDateTime)
* Validation: username ≥3 chars, valid email, password ≥8 chars

#### **Project**

* id (Long)
* name (String)
* description (String)
* createdAt (LocalDateTime)
* Relationships: many‑to‑one User (owner), one‑to‑many Task

#### **Task**

* id (Long)
* title (String)
* description (String)
* status (TODO, IN_PROGRESS, COMPLETED)
* priority (LOW, MEDIUM, HIGH)
* dueDate (LocalDate)
* createdAt (LocalDateTime)
* Relationships: many‑to‑one Project, many‑to‑one User (assignee)

#### **AuditLog**

* id (Long)
* entityType (USER/TASK/PROJECT)
* entityId (Long)
* action (CREATE/UPDATE/DELETE)
* status (SUCCESS/FAILURE)
* createdAt (LocalDateTime)

Used for optional advanced requirement: **Task activity logging (audit trail)**.

---

## API Endpoints

### User Controller

| Method | Endpoint        | Description                |
| ------ | --------------- | -------------------------- |
| POST   | /api/users      | Create user                |
| GET    | /api/users/{id} | Get user by ID             |
| GET    | /api/users      | List all users (paginated) |

### Project Controller

| Method | Endpoint           | Description                   |
| ------ | ------------------ | ----------------------------- |
| POST   | /api/projects      | Create project                |
| GET    | /api/projects/{id} | Get project by ID             |
| GET    | /api/projects      | List all projects (paginated) |
| PUT    | /api/projects/{id} | Update project                |
| DELETE | /api/projects/{id} | Delete project                |

### Task Controller

| Method | Endpoint                        | Description                                |
| ------ | ------------------------------- | ------------------------------------------ |
| POST   | /api/projects/{projectId}/tasks | Create task under project                  |
| GET    | /api/tasks/{id}                 | Get task by ID                             |
| GET    | /api/projects/{projectId}/tasks | Get tasks by project (filter + pagination) |
| PUT    | /api/tasks/{id}                 | Update task                                |
| DELETE | /api/tasks/{id}                 | Delete task                                |
| GET    | /api/tasks/due-today            | Tasks due today                            |
| GET    | /api/users/{userId}/tasks       | Tasks assigned to user                     |

---

## Database Migration (Flyway)

Your `db/migration` folder should include:

* `V1__create_user_project_task_tables.sql`
* `V2__add_audit_log_table.sql`
* Additional incremental versions as needed

Naming convention: **V1__, V2__, V3__, ...**

---

## Running the Application

### 1. Clone the repository

```
git clone <your-repo-url>
cd task-tracker
```

### 2. Build & run with Maven

```
mvn spring-boot:run
```

### 3. Access the application

Backend Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

Frontend (if placed in /static):

```
http://localhost:8080
```

---

## Testing

### Unit Tests

* Services tested with JUnit 5 + Mockito
* Validation tests included

### Integration Tests

* Controller tests using MockMvc
* Repository tests using `@DataJpaTest`

---

## Folder Structure

```
src/main/java/com/example/demo
   ├── controllers
   ├── services
   ├── repositories
   ├── entities
   ├── dtos
   ├── exceptions
   └── config

src/main/resources
   ├── application.properties
   ├── db/migration (Flyway)
   └── static (HTML/CSS/JS)
```

---

## Database Schema Diagram

```
User (1) ──── (∞) Project (1) ──── (∞) Task
                     
                  AuditLog
```

---

## Assumptions

* A user can own many projects.
* A task belongs to exactly one project.
* A task may or may not be assigned to a user.
* Audit logging is optional but supported.
* Frontend is intentionally simple (non-React/Angular).

---

## License

This project is for educational and demonstration purposes.
