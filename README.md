# 🚀 SkillSync – Microservices-Based Mentorship Platform

## 📌 Overview

SkillSync is a **Spring Boot Microservices-based platform** designed to connect learners with mentors.
It enables session booking, skill sharing, notifications, reviews, and authentication with secure access control.

The project follows **modern backend architecture** using:

* Microservices
* API Gateway
* Service Discovery (Eureka)
* Event-driven communication (RabbitMQ)
* Centralized Swagger documentation
* JWT Authentication & Authorization

---

## 🏗️ Architecture

### 🔹 Tech Stack

* **Backend:** Spring Boot, Spring Cloud
* **Database:** MySQL
* **Security:** JWT (Role-Based Access)
* **Messaging:** RabbitMQ
* **Service Discovery:** Eureka Server
* **API Gateway:** Spring Cloud Gateway
* **Documentation:** Swagger (SpringDoc OpenAPI)
* **Tracing (Optional):** Zipkin
* **Containerization:** Docker

---

## 🔧 Microservices

| Service              | Description                                 |
| -------------------- | ------------------------------------------- |
| AUTH-SERVICE         | Handles login, registration, JWT generation |
| USER-SERVICE         | Manages user data                           |
| MENTOR-SERVICE       | Handles mentor profiles                     |
| SKILL-SERVICE        | Manages skills                              |
| SESSION-SERVICE      | Booking & managing sessions                 |
| REVIEW-SERVICE       | Feedback & ratings                          |
| GROUP-SERVICE        | Community/group features                    |
| NOTIFICATION-SERVICE | Sends notifications via RabbitMQ + Email    |
| API-GATEWAY          | Single entry point for all services         |
| EUREKA-SERVER        | Service discovery                           |

---

## 🔄 System Flow

1. User registers/logs in via **Auth Service**
2. JWT Token is generated
3. All requests go through **API Gateway**
4. Gateway validates JWT & forwards request
5. Services communicate:

  * REST (Feign / HTTP)
  * Async via RabbitMQ (notifications)
6. Data stored in MySQL
7. Notifications sent via Email service

---

## 🔐 Authentication & Authorization

* JWT-based authentication
* Role-based access:

  * 👤 USER
  * 🧑‍🏫 MENTOR
  * 🛠️ ADMIN

---

## 📡 API Gateway

* Central routing of all requests
* JWT validation filter
* Swagger aggregation for all services

---

## 📄 Swagger UI (Centralized)

Access all APIs from one place:

👉 http://localhost:8080/swagger-ui.html

---

## 🐇 RabbitMQ Flow (Notification System)

1. Service sends message → Queue
2. Notification Service consumes message
3. Email is sent to user
4. Status saved in DB

---

## 📬 Email Configuration

Uses SMTP (Mailtrap / Gmail App Password)

---

## 🧪 Testing

* Postman for API testing
* Swagger UI for interactive testing
* JUnit + Mockito for unit testing

---

## 🛠️ How to Run

### 🔹 Step 1: Start Required Services

* MySQL
* RabbitMQ
* Eureka Server

---

### 🔹 Step 2: Run Microservices (Order)

1. Eureka Server
2. Auth Service
3. All other services
4. API Gateway

---

### 🔹 Step 3: Access Application

* Gateway: http://localhost:8080
* Eureka: http://localhost:8761
* Swagger: http://localhost:8080/swagger-ui.html

---

## 🐳 Docker Setup (Optional)

```bash
docker build -t apigateway .
docker run -p 8080:8080 apigateway
```

---

## 🔍 Distributed Tracing (Zipkin)

```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

Access:
👉 http://localhost:9411

---

## ⚠️ Common Issues & Fixes

### ❌ 401 / 403 Errors

* Missing JWT token
* Invalid token format

### ❌ Eureka Not Connecting

* Use system IP instead of localhost in Docker

### ❌ Swagger Not Loading

* Allow `/v3/api-docs` in gateway filter

---

## 📊 Features Implemented

✔ Microservices Architecture
✔ API Gateway Routing
✔ JWT Authentication
✔ Role-Based Authorization
✔ RabbitMQ Messaging
✔ Email Notifications
✔ Swagger Documentation
✔ Centralized Logging & Tracing
✔ Docker Support

---

## 📈 Future Enhancements

* Frontend (React / Angular)
* Kubernetes Deployment
* CI/CD Pipeline
* Rate Limiting
* Circuit Breaker (Resilience4j)

---

## 👩‍💻 Author

**Tanisha Rani**

---

## 📚 References
* Spring Boot Documentation
* Spring Cloud Documentation
* RabbitMQ Documentation
* JWT Authentication Best Practices
* Swagger (SpringDoc OpenAPI) Documentation
* Docker Documentation
  * Zipkin Documentation    


