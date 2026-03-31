# 🚀 SkillSync – Backend (Microservices Architecture)

## 📌 Overview

SkillSync is a **backend system for a Peer Learning & Mentor Matching Platform**, built using **Spring Boot Microservices Architecture**.

It enables users to connect with mentors, manage skills, book sessions, and receive notifications in a scalable distributed system.

---

## 🏗️ Architecture

The system follows a **Microservices Architecture** with modular services and centralized infrastructure.

### 🔹 Core Components

* **API Gateway** – Central entry point (Spring Cloud Gateway)
* **Discovery Server (Eureka)** – Service registry
* **Config Server** – Centralized configuration management

---

## 🧩 Microservices

* 🔐 Auth Service
* 👤 User Service
* 👨‍🏫 Mentor Service
* 🧠 Skill Service
* 📅 Session Service
* 👥 Group Service
* ⭐ Review Service
* 🔔 Notification Service

---

## ⚙️ Tech Stack

* Java, Spring Boot
* Spring Cloud (Eureka, Gateway, Config Server)
* REST APIs
* Maven
* MySQL / Relational DB
* RabbitMQ (for notifications)
* Git & GitHub

---

## 🔄 Service Communication

### 🔹 Feign Clients

* Used for **inter-service communication**
* Enables services to call each other via REST in a simplified way

### 🔹 RabbitMQ (Notifications Only)

* Used for **asynchronous communication**
* Handles event-based notifications such as:

  * Session booked
  * Session accepted
  * Reminders

---

## 📂 Project Structure

SkillSync

├── apigateway

├── authservice

├── config-server

├── config-repo

├── discovery-server

├── groupservice

├── mentor

├── notificationservice

├── review-service

├── sessionservice

├── skillService

├── userservice

├── docker-compose.yml

---

## 🚀 Features

* Microservices-based backend architecture
* API Gateway for centralized routing
* Service discovery using Eureka
* Config Server for externalized configuration
* Inter-service communication using Feign Clients
* Asynchronous notification handling using RabbitMQ
* RESTful APIs with layered architecture
* DTO-based request/response handling
* Global exception handling

---

## 🧪 API Testing

* Postman
* Swagger (can be integrated)

---

## 🔐 Security (Planned)

* Spring Security
* JWT Authentication
* Role-based access control

---

## 🚀 Future Enhancements

* JWT authentication implementation
* Docker containerization for all services
* CI/CD pipeline
* Cloud deployment (AWS)

---

## 👩‍💻 Author

Tanisha Rani
GitHub: https://github.com/tanisha-rani


