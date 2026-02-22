# Architecture Decision Records (ADR)
This document tracks the key architectural decisions for **DailyExchange**, the rationale behind them, and the alternatives considered.

## ADR 1: Adoption of Hexagonal Architecture (Ports & Adapters)
* **Status:** Accepted
* **Context:** The market simulator requires complex stochastic logic that must remain isolated from delivery mechanisms (REST, CLI) and persistence technologies.
* **Decision:** We isolated the `Domain` (Business Logic) into a pure Java package. It is accessible only through `Input Ports` and communicates with external systems via `Output Ports`.
* **Consequences:**
* **Pros:** Total testability of the simulation engine without Spring context; easy switching between different database technologies or API protocols.
* **Cons:** Higher initial boilerplate code due to the necessity of Mappers and DTOs.

---

## ADR 2: Decoupling Domain Model from API Response (DTOs)
* **Status:** Accepted
* **Context:** The `SimulationResult` domain object contains raw data required for mathematical calculations. Exposing it directly via REST would create tight coupling with the frontend.
* **Decision:** Use Java **Records** as DTOs and a dedicated `DailyExchangeMapper` within the Infrastructure layer.
* **Motivation:** API Contract protection. If the internal mathematical formulas or domain fields change, the JSON response remains stable because the mapping logic absorbs the changes.

---

## ADR 3: Thread-Safe Formatting (ThreadLocal Formatters)
* **Status:** Accepted
* **Context:** Java’s `NumberFormat` and `DecimalFormat` are not thread-safe. Creating new instances for millions of transactions would cause significant memory pressure (GC overhead).
* **Decision:** Implement `ThreadLocal<NumberFormat>` within the Mapper component.
* **Consequences:** We ensure thread safety in a multi-threaded Spring environment while maximizing performance by reusing formatter instances across the same thread.

---

## ADR 4: Asynchronous Messaging via Apache Kafka
* **Status:** Proposed / In-Progress
* **Context:** Generating billions of atomic transactions based on high-level market dynamics can saturate system resources if done synchronously.
* **Decision:** Use Kafka as an asynchronous buffer between the Market Simulator (Producer) and the Transaction Generator (Consumer).
* **Motivation:** **Backpressure Management**. If the transaction generator is slower than the simulator, Kafka buffers the messages, preventing system failure and allowing the generator to catch up at its own pace.

---

## ADR 5: Envoy Proxy as an Edge Gateway
* **Status:** Accepted
* **Context:** Need for advanced traffic management, load balancing, and observability in a containerized microservices environment.
* **Decision:** Deploy **Envoy Proxy** as the entry point for the Docker Compose stack.
* **Motivation:** Envoy is a modern, cloud-native standard. It provides superior L7 observability, native gRPC support, and robust retry/timeout mechanisms compared to traditional Nginx setups.

---

## ADR 6: Strategy Pattern for Mathematical Modeling
* **Status**: Accepted
* **Context**: Market simulation requires different mathematical approaches (e.g., Geometric Brownian Motion for price, Heston or GARCH for volatility). Hardcoding these would make the system rigid and difficult to upgrade.
* **Decision**: Encapsulate pricing and volatility logic into swappable interfaces (PricingStrategy, VolatilityStrategy).
* **Motivation**: Open/Closed Principle. We can introduce new mathematical models or "Shock" behaviors by simply adding a new class implementation without modifying the existing MarketSimulator engine. This is critical for financial software where models are frequently refined or replaced based on market conditions.

---

## ADR 7: Ephemeral Infrastructure and "Office Hours" Availability
* **Status**: Accepted
* **Context**: To minimize the attack surface and optimize resource consumption for the demo environment, the system is hosted on physical hardware (Raspberry Pi 4B + NVMe SSD).
* **Decision**: The infrastructure operates strictly during business hours (09:00 - 18:00 CET). Outside of this window, the hardware is physically or logically disconnected.
* **Motivation**: Security by Isolation. Reducing the online time significantly lowers the probability of automated scans and unauthorized access attempts. It also simulates a real-world scenario where high-security financial environments have restricted access windows.

---

## ADR 8: One-Hour Data Retention Policy (Self-Destructing Data)
* **Status**: Accepted
* **Context**: The project handles simulated but sensitive-looking financial data. We need to ensure that user sessions and simulation results do not persist indefinitely.
* **Decision**: Implementation of an Ephemeral Account System. Each temporary user account and all its associated data (market states, transaction logs) are programmatically purged after 60 minutes.
* **Implementation Details**:
  * JWT Expiration: Authentication tokens are strictly valid for 1 hour.
  * Redis TTL: In-memory state utilizes Time-To-Live (TTL) settings for automatic expiration.
  * Scheduled Cleanup: A Spring @Scheduled task executes a "Hard Delete" on the SQL database for any records exceeding the 60-minute threshold.
* **Motivation**: Privacy by Design. This ensures the system remains clean, performant, and compliant with the strictest data minimization principles (GDPR mindset), even in a demo environment.