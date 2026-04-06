# dailyexchange

[![codecov](https://codecov.io/gh/dennisromano/bullnbear/branch/main/graph/badge.svg?flag=dailyexchange)](https://codecov.io/gh/dennisromano/bullnbear)

A microservice that simulates daily financial market dynamics using stochastic mathematical models. Given a set of initial parameters, it runs a day-by-day simulation of price, volatility, quantity, and shock events for a fictional asset.

---

## 📉 Mathematical Models

The engine leverages two primary stochastic processes to generate synthetic market data:

- **Geometric Brownian Motion (GBM):** Models price evolution with drift ($\mu$) and volatility ($\sigma$), including the Itô correction ($-0.5\sigma^2$).
- **Mean-Reverting Volatility (Heston-inspired):** Models volatility as a stochastic process that reverts toward a long-run target level ($\kappa$, vol-of-vol).

Shock events are handled by the `StochasticShockProvider`, which introduces probabilistic spikes in volatility to simulate real-world market stress.

---

## 🏗️ Architecture

The service follows a **Hexagonal Architecture (Ports & Adapters)** pattern to ensure a decoupled and testable core logic:

```
src/main/java/org/dennisromano/dailyexchange/
├── domain/
│   ├── model/
│   └── ports/
├── application/
└── infrastructure/
    ├── generators/
    ├── mathematics/
    ├── reporting/
    └── rest/
        ├── controller/ 
        ├── dto/
        ├── mapper/
        └── service/
````

### 🏛️ Architectural Governance

This project follows a disciplined approach to decision-making. Key architectural choices are documented via **Architecture Decision Records (ADRs)**:

| ID                                                                                              | Title                      | Rationale                                                                                                                |
|-------------------------------------------------------------------------------------------------|----------------------------|--------------------------------------------------------------------------------------------------------------------------|
| [ADR 01](./../../docs/DECISIONS.md#adr-1-adoption-of-hexagonal-architecture-ports--adapters)    | **Hexagonal Architecture** | Isolates complex stochastic calculus from Spring Boot infrastructure, ensuring pure domain testability.                  |
| [ADR 02](./../../docs/DECISIONS.md#adr-2-decoupling-domain-model-from-api-response-dtos)        | **DTO Mapping Strategy**   | Protects the API contract by decoupling Internal Domain Records from REST responses via a dedicated Mapping layer.       |
| [ADR 03](./../../docs/DECISIONS.md#adr-3-thread-safe-formatting-threadlocal-formatters)         | **Thread-Safe Formatting** | Optimizes performance using ThreadLocal for high-frequency financial number formatting, reducing GC overhead.            |
| [ADR 06](./../../docs/DECISIONS.md#adr-6-strategy-pattern-for-mathematical-modeling)            | **Strategy Pattern**       | Enables swappable mathematical engines (e.g., GBM, Heston) without modifying the core simulation orchestrator.           |
| [ADR 09](./../../docs/DECISIONS.md#adr-9-webmvctest-as-the-testing-strategy-for-the-rest-layer) | **Isolated Web Testing**   | Implements @WebMvcTest for fast, sliced testing of the REST layer, adhering to the Test Pyramid principles.              |
| [ADR 10](./../../docs/DECISIONS.md#adr-10-jacoco-as-coverage-tool-with-80-minimum-threshold)    | **JaCoCo Quality Gate**    | Enforces a strict 80% instruction coverage threshold at build time to ensure the reliability of financial models.        |
| [ADR 11](./../../docs/DECISIONS.md#adr-12-openapi-3-swagger-for-documentation)                  | **OpenAPI (Swagger)**      | Establishes a "Single Source of Truth" for the API contract, providing a live, semantic sandbox for integration testing. |

---

## 🛠️ Tech Stack

| Technology        | Version |
|-------------------|---------|
| Java              | 25      |
| Spring Boot       | 4.0.5   |
| Gradle            | 9.4.1   |
| SpringDoc OpenAPI | 3.0.2   |
| JaCoCo            | 0.8.12  |
| JUnit             | 6.0.3   |
| Mockit            | 5.20.0  |

---

## 🚀 Running Locally

```bash
# From the monorepo root
./gradlew :services:dailyexchange:bootRun
````

The service will be available at `http://localhost:8080`.

-----

## 🔌 API Documentation

This microservice adopts a **Contract-First** approach. The API documentation is automatically generated and synchronized with the implementation via Swagger UI.

### How to access:

1.  Start the service (see [Running Locally](README.md#-running-locally)).
2.  Navigate to: [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)

-----

## 🧪 Running Tests & Coverage

```bash
# Run all tests
./gradlew :services:dailyexchange:test

# Generate JaCoCo report
./gradlew :services:dailyexchange:test :services:dailyexchange:jacocoTestReport
```

*Note: Minimum instruction coverage is enforced at **80%** via `jacocoTestCoverageVerification`.*