# Bull&Bear - High-Frequency Market Simulator
**Bull&Bear** is a distributed system designed to simulate complex market dynamics and high-frequency trading (HFT) environments. The project demonstrates the application of **Hexagonal Architecture**, event-driven patterns, and high-throughput data processing.

---

## Architectural Overview
The system is designed following the **Clean Architecture** principles to ensure that the core business logic (Stochastic Market Models) remains independent of infrastructure details (REST, Kafka, Database).

### Core Components
1. **Market Dynamics (Service 1 - dailyexchange - *In Progress*):** The "Brain". Orchestrates stochastic processes (GBM, Volatility Mean Reversion) to determine daily market trends.
2. **Trade Generator (Service 2 - *TBD*):** The "Engine". Consumes market trends via **Kafka** and generates millions of atomic transactions using **Redis** as a low-latency state store.
3. **Infrastructure (*TBD*):** Orchestrated via **Docker** and **Envoy Proxy** as an L7 edge gateway for observability and resilience.

---

## Tech Stack

| Layer              | Technology     | Key Reason                                                             |
|--------------------|----------------|------------------------------------------------------------------------|
| **Language**       | Java 25        | Utilizing Records, Pattern Matching, and Virtual Threads.              |
| **Framework**      | Spring Boot 4  | Industry standard for microservices with native GraalVM support.       |
| **Architecture**   | Hexagonal      | Strict decoupling of business logic from external drivers.             |
| **Database (SQL)** | PostgreSQL 18	 | ACID-compliant storage for historical audits and relational integrity. |
| **Messaging**      | Apache Kafka   | Handles backpressure during massive transaction bursts.                |
| **Caching**        | Redis          | Sub-millisecond state management for real-time market snapshots.       |
| **Edge Proxy**     | Envoy          | Advanced traffic management and observability (Cloud-Native standard). |

---

## Key Design Patterns

### 1. Hexagonal Architecture
The domain layer is a pure Java module. It communicates with the outside world only through interfaces (Ports). This allows us to switch from a REST controller to a CLI or a Cron task without touching the simulation logic.

### 2. Strategy Pattern
Market pricing and volatility are implemented as swappable strategies (e.g., `PricingStrategy`). This allows for easy integration of different mathematical models like Heston or Black-Scholes.

### 3. Thread-Safe Mapping
Infrastructure mappers use `ThreadLocal<NumberFormat>` to ensure high-performance, thread-safe formatting for financial reports without the overhead of creating new formatter instances for every request.

---

## Getting Started

### TBD

---

## Roadmap & Upcoming Features

## General
* [ ] **Docker**
* [ ] **Kafka**
* [ ] **Redis**
* [ ] **PostgreSQL**
* [ ] **Demo enviroment**
* [ ] **Envoy**

### Service 1 - dailyexchange - Market Dynamics
* [x] **Mathematics Engine**
* [x] **Hexagonal Architecture**
* [x] **Microservice Structure**
* [x] **Rest API**

### Service 2 - Kafka-driven Transaction Generator
* [ ] TBD

### Frontend -  Real-time Market Dashboard in **Flutter**
* [ ] TBD

---

## Author
**Dennis Romano** - *Software Architect*

* LinkedIn: www.linkedin.com/in/dennis-romano
* Portfolio: TBD