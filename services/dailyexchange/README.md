# dailyexchange

[![codecov](https://codecov.io/gh/dennisromano/bullnbear/branch/main/graph/badge.svg?flag=dailyexchange)](https://codecov.io/gh/dennisromano/bullnbear)

A microservice that simulates daily financial market dynamics using stochastic mathematical models. Given a set of initial parameters, it runs a day-by-day simulation of price, volatility, quantity, and shock events for a fictional asset.

## Architecture

The service follows a **Hexagonal Architecture (Ports & Adapters)** pattern:

```
src/main/java/org/dennisromano/dailyexchange/
├── domain/
│   ├── model/          # Core domain entities (MarketState, SimulationResult, ShockEvent, ...)
│   └── ports/          # Input/output interfaces (DailyExchangeInputPort, VolatilityStrategy, ...)
├── application/
│   └── MarketSimulator.java        # Orchestrates the simulation step-by-step
└── infrastructure/
    ├── DailyExchangeSpringBootApplication.java
    ├── generators/     # StochasticShockProvider
    ├── mathematics/    # GeometricBrownianMotionStrategy, MeanRevertingVolatilityStrategy
    ├── reporting/      # MarketReporter
    └── rest/
        ├── controller/ # DailyExchangeRestController
        ├── dto/        # DailyExchangeRequest, DailyExchangeResponse
        ├── mapper/     # DailyExchangeMapper
        └── service/    # DailyExchangeSimulator (implements DailyExchangeInputPort)
```

## Mathematical Models

The simulation uses two stochastic processes:

- **Geometric Brownian Motion (GBM)** — models price evolution with drift (μ) and volatility (σ), including the Itô correction (−0.5σ²)
- **Mean-Reverting Volatility (Heston-inspired)** — models volatility as a stochastic process that reverts toward a target level (κ, vol-of-vol)

Shock events are generated probabilistically by `StochasticShockProvider` and temporarily increase volatility when triggered.

## API

### `POST /api/v1/dailyexchange/simulate`

Runs a market simulation and returns a day-by-day result list.

**Request body:**

```json
{
  "simulationDays": 252,
  "tradingDaysPerYear": 252,
  "initialPrice": 100.0,
  "initialQuantity": 1000.0,
  "targetVolatility": 0.2,
  "targetMu": 0.07,
  "kappa": 3.0,
  "volOfVol": 0.3,
  "shockProbability": 0.02
}
```

| Field | Type | Description |
|---|---|---|
| `simulationDays` | int | Number of trading days to simulate |
| `tradingDaysPerYear` | double | Used to compute the time step dt = 1/tradingDaysPerYear |
| `initialPrice` | double | Starting asset price |
| `initialQuantity` | double | Starting asset quantity |
| `targetVolatility` | double | Long-run target volatility (σ̄) |
| `targetMu` | double | Target annual drift (expected return) |
| `kappa` | double | Mean-reversion speed for volatility |
| `volOfVol` | double | Volatility of volatility |
| `shockProbability` | double | Daily probability of a shock event (0.0–1.0) |

**Response body (array):**

```json
[
  {
    "day": "Day 1",
    "companyName": "PROTOCOL",
    "operationType": "BULL",
    "volatility": "18.45%",
    "variation": "+1.23%",
    "price": "101.23",
    "quantity": "1002.14",
    "capital": "101,453.22",
    "shockEventTitle": null,
    "shockEventIntensity": null
  }
]
```

## Tech Stack

| Technology | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.4 |
| Gradle | 9.4.1 |
| JaCoCo | (via Spring Boot BOM) |
| JUnit | (via spring-boot-starter-webmvc-test) |
| Mockito | (via spring-boot-starter-webmvc-test) |

## Running Locally

```bash
# From the monorepo root
./gradlew :services:dailyexchange:bootRun
```

The service starts on `http://localhost:8080`.

## Running Tests

```bash
# From the monorepo root
./gradlew :services:dailyexchange:test

# With coverage report (output: services/dailyexchange/build/reports/jacoco/test/html/index.html)
./gradlew :services:dailyexchange:test :services:dailyexchange:jacocoTestReport
```

## Coverage

Minimum coverage enforced: **80%** (via `jacocoTestCoverageVerification`).
