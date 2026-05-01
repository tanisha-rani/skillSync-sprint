# SkillSync Architecture Diagrams

These diagrams are tailored for the SkillSync peer learning and mentor matching platform.

## 1. Microservices Architecture

Use this diagram when explaining the backend design, service discovery, API gateway routing, databases, and async notification flow.

![SkillSync Microservices Architecture](./skillsync-microservices-architecture.svg)

```mermaid
flowchart TB
    Client["Web Client\nReact + Vite"] -->|/api REST + JWT| Gateway["API Gateway\nSpring Cloud Gateway\nPort 8080"]

    subgraph Infra["Platform Infrastructure"]
        Config["Config Server\nPort 8888\nconfig-repo"]
        Eureka["Discovery Server\nEureka\nPort 8761"]
        Zipkin["Zipkin Tracing\nPort 9411"]
        Rabbit["RabbitMQ\nAMQP 5672\nManagement 15672"]
        MySQL["MySQL Server\nHost Port 3307"]
    end

    Gateway -->|load-balanced routes| Auth["Auth Service\nPort 8087"]
    Gateway -->|JWT protected| User["User Service\nPort 8081"]
    Gateway -->|JWT protected| Mentor["Mentor Service\nPort 8082"]
    Gateway -->|JWT protected| Skill["Skill Service\nPort 8083"]
    Gateway -->|JWT protected| Session["Session Service\nPort 8084"]
    Gateway -->|JWT protected| Group["Group Service\nPort 8085"]
    Gateway -->|JWT protected| Review["Review Service\nPort 8086"]
    Gateway -->|JWT protected| Notification["Notification Service\nPort 8089"]

    Config -. config .-> Gateway
    Config -. config .-> Auth
    Config -. config .-> User
    Config -. config .-> Mentor
    Config -. config .-> Skill
    Config -. config .-> Session
    Config -. config .-> Group
    Config -. config .-> Review
    Config -. config .-> Notification

    Gateway -. registers/discovers .-> Eureka
    Auth -. registers .-> Eureka
    User -. registers .-> Eureka
    Mentor -. registers .-> Eureka
    Skill -. registers .-> Eureka
    Session -. registers .-> Eureka
    Group -. registers .-> Eureka
    Review -. registers .-> Eureka
    Notification -. registers .-> Eureka

    Auth -->|auth_db| MySQL
    User -->|users_db| MySQL
    Mentor -->|mentor_db| MySQL
    Skill -->|skill_db| MySQL
    Session -->|session_db| MySQL
    Group -->|group_db| MySQL
    Review -->|review_db| MySQL
    Notification -->|notifications_db| MySQL

    Auth -->|Feign| User
    Auth -->|Feign| Notification
    Mentor -->|Feign| User
    Mentor -->|Feign| Notification
    Session -->|Feign| User
    Session -->|Feign| Mentor
    Session -->|Feign| Notification
    Review -->|Feign| Session
    Review -->|Feign| Mentor
    Review -->|Feign| Notification
    Group -->|Feign| User
    Notification -->|Feign| Session

    Notification -->|notification.exchange\nnotification.email| Rabbit
    Rabbit -->|notificationQueue| Notification

    Gateway -. traces .-> Zipkin
    Auth -. traces .-> Zipkin
    User -. traces .-> Zipkin
    Mentor -. traces .-> Zipkin
    Skill -. traces .-> Zipkin
    Session -. traces .-> Zipkin
    Group -. traces .-> Zipkin
    Review -. traces .-> Zipkin
    Notification -. traces .-> Zipkin
```

## 2. Deployment / Runtime Architecture

Use this diagram when explaining how the local system runs through Docker Compose and service ports.

![SkillSync Deployment Architecture](./skillsync-deployment-architecture.svg)

```mermaid
flowchart LR
    Browser["Browser\nReact UI"] -->|http://localhost:5173| Vite["Vite Dev Server\n/api proxy"]
    Vite -->|http://localhost:8080| Gateway["skillsync-gateway\nAPI Gateway"]

    subgraph Docker["Docker Compose Runtime"]
        Gateway --> Auth["skillsync-auth\n8087"]
        Gateway --> User["skillsync-user\n8081"]
        Gateway --> Mentor["skillsync-mentor\n8082"]
        Gateway --> Skill["skillsync-skill\n8083"]
        Gateway --> Session["skillsync-session\n8084"]
        Gateway --> Group["skillsync-group\n8085"]
        Gateway --> Review["skillsync-review\n8086"]
        Gateway --> Notification["skillsync-notification\n8089"]

        Eureka["skillsync-discovery\nEureka 8761"]
        Config["Config Server\n8888"]
        MySQL[("skillsync-mysql\n3307 -> 3306")]
        Rabbit["skillsync-rabbitmq\n5672 / 15672"]
        Zipkin["skillsync-zipkin\n9411"]
    end

    Gateway -. discovery .-> Eureka
    Auth -. discovery .-> Eureka
    User -. discovery .-> Eureka
    Mentor -. discovery .-> Eureka
    Skill -. discovery .-> Eureka
    Session -. discovery .-> Eureka
    Group -. discovery .-> Eureka
    Review -. discovery .-> Eureka
    Notification -. discovery .-> Eureka

    Config -. centralized YAML .-> Gateway
    Config -. centralized YAML .-> Auth
    Config -. centralized YAML .-> User
    Config -. centralized YAML .-> Mentor
    Config -. centralized YAML .-> Skill
    Config -. centralized YAML .-> Session
    Config -. centralized YAML .-> Group
    Config -. centralized YAML .-> Review
    Config -. centralized YAML .-> Notification

    Auth --> MySQL
    User --> MySQL
    Mentor --> MySQL
    Skill --> MySQL
    Session --> MySQL
    Group --> MySQL
    Review --> MySQL
    Notification --> MySQL

    Notification <--> Rabbit
    Gateway -. spans .-> Zipkin
    Auth -. spans .-> Zipkin
    User -. spans .-> Zipkin
    Mentor -. spans .-> Zipkin
    Skill -. spans .-> Zipkin
    Session -. spans .-> Zipkin
    Group -. spans .-> Zipkin
    Review -. spans .-> Zipkin
    Notification -. spans .-> Zipkin
```

