# IT Service - Milestone 2 Design

This document updates the DDD Domain Model and C4 diagrams for Milestone 2.

## DDD Domain Model

Aggregate invariant: a resolution step can only be added to an existing ticket whose status is not `CLOSED`.

```mermaid
classDiagram
    class Ticket {
        <<Aggregate Root>>
        String ticketId
        String title
        String description
        String customerId
        String staffId
        String assetId
        TicketStatus status
        Priority priority
    }

    class ResolutionStep {
        String stepId
        String ticketId
        Integer stepNumber
        String actionTaken
        String result
        LocalDateTime createdAt
    }

    class Customer {
        String customerId
        String firstName
        String lastName
        String email
        String department
    }

    class Staff {
        String staffId
        String firstName
        String lastName
        String email
        StaffRole staffRole
    }

    class Asset {
        String assetId
        AssetType type
        AssetStatus status
    }

    class TicketStatus {
        <<enumeration>>
        NEW
        IN_PROGRESS
        CLOSED
    }

    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
    }

    Ticket "1" --> "0..*" ResolutionStep : contains
    Ticket --> Customer : references customerId
    Ticket --> Staff : references staffId
    Ticket --> Asset : references assetId
    Ticket --> TicketStatus
    Ticket --> Priority
```

## C4 Level 1 - System Context

```mermaid
flowchart LR
    User["User / IT Support Agent"]
    System["IT Service System"]

    User -->|"Uses REST API on port 8080"| System

    System -->|"Manages customers"| CustomerData["Customer Data"]
    System -->|"Manages staff"| StaffData["Staff Data"]
    System -->|"Manages assets"| AssetData["Asset Data"]
    System -->|"Orchestrates incidents"| IncidentData["Incident Data"]
```

## C4 Level 2 - Containers

```mermaid
flowchart TB
    User["User / API Client"]

    Gateway["API Gateway<br/>Spring Boot<br/>Port 8080 exposed"]

    CustomerService["Customer Service<br/>Spring Boot"]
    StaffService["Staff Service<br/>Spring Boot"]
    AssetService["Asset Service<br/>Spring Boot"]
    IncidentService["Incident Service<br/>Aggregator / Orchestrator<br/>Spring Boot"]

    CustomerDb["mysql1<br/>customer-db"]
    StaffDb["mysql2<br/>staff-db"]
    AssetDb["postgres1<br/>asset-db"]
    IncidentDb["mongodb-incident<br/>incident-db"]

    CustomerGui["phpmyadmin1"]
    StaffGui["phpmyadmin2"]
    AssetGui["pgadmin"]
    IncidentGui["mongo-express-incident"]

    User -->|"HTTP / JSON"| Gateway

    Gateway -->|"GET ALL, GET, POST, PUT, DELETE customers"| CustomerService
    Gateway -->|"GET ALL, GET, POST, PUT, DELETE staff"| StaffService
    Gateway -->|"GET ALL, GET, POST, PUT, DELETE assets"| AssetService
    Gateway -->|"GET ALL, GET, POST, PUT, DELETE tickets<br/>resolution-step endpoints"| IncidentService

    IncidentService -->|"Validates customerId"| CustomerService
    IncidentService -->|"Validates staffId"| StaffService
    IncidentService -->|"Validates assetId"| AssetService

    CustomerService --> CustomerDb
    StaffService --> StaffDb
    AssetService --> AssetDb
    IncidentService --> IncidentDb

    CustomerGui --> CustomerDb
    StaffGui --> StaffDb
    AssetGui --> AssetDb
    IncidentGui --> IncidentDb
```

## Implementation Traceability

| Requirement | Implementation |
| --- | --- |
| API Gateway exposes all downstream endpoints | `apigateway` controllers for assets, customers, staff, tickets, and resolution steps |
| Aggregator orchestrates all three low-level services | `incident-service` calls customer, staff, and asset domain clients before creating/updating tickets |
| Aggregator persists aggregate in Mongo | `incident-service` uses Spring Data MongoDB and `mongodb-incident` in Docker |
| Aggregate invariant | `ResolutionStepServiceImpl` rejects resolution steps for closed tickets |
| Only API Gateway exposed outside Docker | `Docker-compose.yaml` only publishes `8080:8080` |
| Required testing types | Repository integration, controller integration, service unit, and controller unit tests are present |
| Bash script tests through API Gateway | `api-gateway-tests.bash` uses only `http://localhost:8080/api/v1` |
