# 🏨 Hotel Booking System

A production-ready backend for an **Airbnb-inspired Hotel Booking System** built with **Spring Boot**, **PostgreSQL**, **Spring Security**, **JWT Authentication**, **Stripe**, and **Docker**.

The application enables users to browse hotels, manage properties, handle room inventory, book accommodations, process payments, and securely manage bookings through REST APIs.

---

## ✨ Features

### 🔐 Authentication & Authorization

- User Registration
- User Login
- JWT Authentication
- Role-Based Authorization
- BCrypt Password Encryption

### 🏨 Hotel Management

- Create Hotel
- Update Hotel
- Delete Hotel
- Hotel Details
- Hotel Ownership

### 🛏 Room Management

- Add Rooms
- Update Rooms
- Delete Rooms
- Room Amenities
- Room Images

### 📅 Inventory Management

- Daily Inventory
- Room Availability
- Open / Close Rooms
- Dynamic Pricing
- Surge Pricing

### 📖 Booking

- Search Available Rooms
- Book Rooms
- Booking History
- Booking Cancellation

### 💳 Payment

- Stripe Payment Integration
- Stripe Webhook Support

### ⚙️ Developer Features

- RESTful APIs
- OpenAPI / Swagger
- Global Exception Handling
- DTO Mapping using MapStruct
- Environment-based Configuration
- Docker Compose
- PostgreSQL
- Stripe CLI Webhook Forwarding

---

# 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 18 |
| Build Tool | Maven |
| API Documentation | Swagger / OpenAPI |
| Payment | Stripe |
| Object Mapping | MapStruct |
| Containerization | Docker & Docker Compose |

---

# 📁 Project Structure

```
src/main/java/com/my_space/airbnb_clone
│
├── advice
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── mapper
├── repository
├── security
├── service
├── strategy
├── util
│
└── AirbnbCloneApplication.java
```

---

# 🚀 Getting Started

## Prerequisites

Install the following:

- Java 21
- Maven 3.9+
- Docker Desktop
- Git

---

## Clone Repository

```bash
git clone https://github.com/m-ohit-s/Hotel-Booking-System.git

cd Hotel-Booking-System
```

---

# 🔐 Environment Variables

Create a `.env` file in the project root.

```bash
cp .env.example .env
```

Example:

```env
DB_URL=jdbc:postgresql://postgres:5432/airbnb_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=your-secret-key

STRIPE_SECRET_KEY=your-stripe-secret

STRIPE_WEBHOOK_SECRET_KEY=your-webhook-secret
```

> **Note**
>
> Never commit your `.env` file.
>
> Commit only `.env.example`.

---

# 🐳 Running the Application

The project is fully containerized using Docker Compose.

Start all services:

```bash
docker compose up --build
```

This starts:

- Spring Boot Application
- PostgreSQL 18
- Stripe CLI

Stop all services:

```bash
docker compose down
```

View logs:

```bash
docker compose logs -f
```

---

# 🌐 Application URLs

Backend

```
http://localhost:8080/api/v1
```

Swagger UI

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

OpenAPI Docs

```
http://localhost:8080/api/v1/v3/api-docs
```

---

# 🔐 Authentication

Authenticate using JWT.

Include the Access Token in every request.

```
Authorization: Bearer <ACCESS_TOKEN>
```

---

# 💳 Stripe Webhook

Stripe CLI automatically forwards webhook events to:

```
http://host.docker.internal:8080/api/v1/webhook/payment
```

No additional Stripe configuration is required for local development once the containers are running.

---

# 🏗 Architecture

```
                Client
                   │
                   ▼
         Spring Boot REST API
                   │
     ┌─────────────┴──────────────┐
     ▼                            ▼
 PostgreSQL                 Stripe Webhooks
     │                            ▲
     └──────── Docker Compose ────┘
```

---

# 🧪 Running Tests

Run all tests

```bash
./mvnw test
```

---

# 📦 Build Docker Image

Build only the application image:

```bash
docker build -t hotel-booking-system .
```

Run the image:

```bash
docker run \
    --env-file .env \
    -p 8080:8080 \
    hotel-booking-system
```

---

# 📄 API Documentation

Interactive Swagger documentation is available after the application starts.

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository.

2. Create a feature branch.

```bash
git checkout -b feature/my-feature
```

3. Commit your changes.

```bash
git commit -m "Add awesome feature"
```

4. Push your branch.

```bash
git push origin feature/my-feature
```

5. Open a Pull Request.

---

# 👨‍💻 Author

**Mohit Sadhwani**

Software Engineer

GitHub: https://github.com/m-ohit-s

---

⭐ If you found this project useful, consider giving it a star.