# 🚀 SkillSync – Backend (Microservices Architecture)

## 📌 Overview

SkillSync is a **backend system for a Peer Learning & Mentor Matching Platform**, built using **Spring Boot Microservices Architecture**.

The platform connects learners with mentors, enables session booking, skill management, and supports scalable service communication.

This repository contains the **backend implementation only**.

---

## 🏗️ Architecture

The system follows **Microservices Architecture** with service discovery and centralized routing.

### 🔹 Core Components

* **API Gateway** – Central entry point for all client requests
* **Discovery Server (Eureka)** – Service registry for dynamic service discovery
* **Independent Microservices**:

    * Mentor Service
    * Skill Service
    * User Service
    * Session Service *(in progress)*

---

## ⚙️ Tech Stack

* Java, Spring Boot
* Spring Cloud (Eureka, API Gateway)
* REST APIs
* Maven
* MySQL / Relational Database
* Git & GitHub

---

## 📂 Project Structure

SkillSync

├── apigateway

├── discovery-server

├── mentor

├── skillService

├── sessionService 

├── userservice

---

## 🔀 Git Branch Strategy

Each microservice is maintained in a separate branch:

* `mentor-service`
* `skill-service`
* `userservice`
* `api-gateway`
* `discovery-server`
* `session-service` *(under development)*

---

## 🚀 Features Implemented

### 🔐 Authentication & Authorization (Planned)

* JWT-based authentication (future scope)
* Role-based access (Learner, Mentor, Admin)

### 👤 User Service

* User profile management
* CRUD operations for users

### 👨‍🏫 Mentor Service

* Mentor profile creation
* Experience & skill management
* Mentor-related business logic

### 🧠 Skill Service

* Skill creation & management
* Skill tagging for mentors

### 📡 API Gateway

* Centralized routing
* Request forwarding to microservices

### 🧭 Discovery Server

* Service registration using Eureka
* Dynamic service lookup

### 📅 Session Service *(In Progress)*

* Session booking & scheduling
* Accept/reject workflow

---

## 🔄 Microservices Communication

* Services communicate using **REST APIs**
* Future scope: **Event-driven architecture using RabbitMQ**

---

## 🧪 API Testing

Test endpoints using:

* Postman
* Swagger *(can be integrated)*

---

## ⚡ Key Highlights

* Microservices-based scalable backend
* Layered architecture (Controller → Service → Repository)
* DTO-based request/response handling
* Global exception handling implemented
* Modular and maintainable code structure

---

## 🔐 Security (Planned)

* Spring Security integration
* JWT-based authentication
* Role-based authorization

---

## 🚀 Future Enhancements

* JWT Authentication & Security
* RabbitMQ for event-driven communication
* Docker containerization
* CI/CD pipeline (GitHub Actions)
* Cloud deployment (AWS)
* Notification & Review Services

---

## 👩‍💻 Author

Tanisha Rani
GitHub: https://github.com/tanisha-rani

---

## 📌 Note

This repository contains only the **backend implementation** of SkillSync.
Frontend (Angular) and advanced services are part of extended system design.
