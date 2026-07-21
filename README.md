# Renewable Energy Monitoring System

A microservices-based platform for monitoring solar panels, wind turbines, and battery storage, with automated energy distribution and fault detection.

---

## What It Does

- Manages a fleet of **solar panels**, **wind turbines**, and **batteries** via dedicated microservices
- Aggregates renewable generation (solar + wind) and distributes it to meet demand
- Stores excess renewable energy in batteries; discharges batteries when generation falls short
- Automatically detects equipment faults (zero output, low charge, under-maintenance devices)
- Exposes everything through a single **API Gateway** with one unified URL

---

## Architecture

```
        Client / Postman
              │
              ▼
     ┌─────────────────┐
     │  API Gateway    │  :8080
     │  /solar /wind   │
     │  /battery       │
     │  /distribution  │
     └────────┬────────┘
              │
   ┌──────────┼──────────────┬──────────────┐
   ▼          ▼              ▼              ▼
┌──────┐  ┌──────┐      ┌─────────┐   ┌────────────┐
│Solar │  │ Wind │      │ Battery │   │ Distribu-  │
│ 8081 │  │ 8082 │      │  8083   │   │ tion 8084  │
└──┬───┘  └──┬───┘      └────┬────┘   └─────┬──────┘
   ▼         ▼              ▼              │
solar_db  wind_db       battery_db    distribution_db
                          ▲              ▲
                          └──────────────┘
                        (RestTemplate calls)
```

`energy-distribution-service` calls the three device services via RestTemplate to aggregate generation and trigger charge/discharge.

---

## Tech Stack

- **Java 17**, **Spring Boot 3.2.5**
- **Spring Cloud Gateway 2023.0.1** (gateway)
- **Spring Data JPA** + **MySQL 8**
- **Lombok 1.18.46**
- **springdoc-openapi 2.5.0** (Swagger UI)
- **JUnit 5 + Mockito**

---

## Services

| Service | Port | Database | Responsibility |
|---|---|---|---|
| `solar-panel-service` | 8081 | `solar_db` | Solar panel CRUD + hourly generation |
| `wind-turbine-service` | 8082 | `wind_db` | Wind turbine CRUD + hourly output |
| `battery-service` | 8083 | `battery_db` | Battery CRUD + charge/discharge |
| `energy-distribution-service` | 8084 | `distribution_db` | Distribution + fault detection + reports |
| `api-gateway` | 8080 | — | Routes `/solar`, `/wind`, `/battery`, `/distribution` |

---

## Project Layout

```
RenewableEnergyMonitoringSystem/
├── solar-panel-service/         # CRUD + generation recording
├── wind-turbine-service/        # CRUD + output recording
├── battery-service/             # CRUD + charge/discharge
├── energy-distribution-service/ # Aggregates + distributes + fault detection
├── api-gateway/                 # Single entry point, routes traffic
├── sql/                         # Database creation scripts
│   ├── 01_solar_db.sql
│   ├── 02_wind_db.sql
│   ├── 03_battery_db.sql
│   ├── 04_distribution_db.sql
│   └── ER_DIAGRAM.md
├── postman/
│   └── Renewable-Energy-Monitoring-System.postman_collection.json
└── README.md
```

Each service follows the same package structure:

```
src/main/java/com/renewable/<domain>/
├── <Domain>ServiceApplication.java
├── controller/    # REST endpoints
├── service/       # Service interface
├── service/impl/  # Service implementation
├── repository/    # Spring Data JPA
├── entity/        # JPA entities + enums
├── dto/           # Request/response DTOs
├── exception/     # Custom exceptions + global handler
├── config/        # OpenAPI config
├── util/          # Entity ↔ DTO mappers
└── validation/    # Custom validators
```

---

## Setup

### 1. Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8.x running on port 3306
- IntelliJ IDEA (recommended)

### 2. Create Databases

Hibernate will auto-create each schema on startup. To create them manually for a clean setup, run the SQL scripts in `sql/` against your MySQL instance in numeric order (`01_solar_db.sql` → `04_distribution_db.sql`).

### 3. Configure Credentials

In each service's `application.properties`, set your MySQL username and password:

```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## Run

### Option A — IntelliJ IDEA (recommended)

1. Open the parent `RenewableEnergyMonitoringSystem` folder. IntelliJ will detect each `pom.xml` as a separate module.
2. Locate the main class for each service:
   - `solar-panel-service/src/main/java/com/renewable/solar/SolarPanelServiceApplication.java`
   - `wind-turbine-service/src/main/java/com/renewable/wind/WindTurbineServiceApplication.java`
   - `battery-service/src/main/java/com/renewable/battery/BatteryServiceApplication.java`
   - `energy-distribution-service/src/main/java/com/renewable/distribution/EnergyDistributionServiceApplication.java`
   - `api-gateway/src/main/java/com/renewable/gateway/ApiGatewayApplication.java`
3. Right-click each `…Application.java` → **Run**.

Recommended startup order: **Solar → Wind → Battery → Distribution → Gateway**. (Distribution needs the three device services running; the gateway just needs everything else up.)

### Option B — IDE Run Configurations

Create one Spring Boot run configuration per service in your IDE, each pointing at the corresponding main class. Then launch them in the order above.

### After Startup

| Access | URL |
|---|---|
| Direct to a service | `http://localhost:<port>` |
| Via gateway | `http://localhost:8080/solar`, `/wind`, `/battery`, `/distribution` |
| Health check | `http://localhost:<port>/actuator/health` |
| Gateway routes | `http://localhost:8080/actuator/gateway/routes` |

### Tests

Each service includes:
- A service-layer test (`*ServiceImplTest.java`)
- A controller-layer test (`*ControllerTest.java`)

Run them from your IDE's test runner, or use the Maven test runner in IntelliJ.

---

## API Reference

### Solar Panel Service — `:8081`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/solar` | Register a panel |
| GET | `/api/solar` | List all panels |
| GET | `/api/solar/{id}` | Get a panel |
| PUT | `/api/solar/{id}` | Update a panel |
| DELETE | `/api/solar/{id}` | Deactivate a panel |
| POST | `/api/solar/{id}/generation` | Record hourly generation |

### Wind Turbine Service — `:8082`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/wind` | Register a turbine |
| GET | `/api/wind` | List all turbines |
| GET | `/api/wind/{id}` | Get a turbine |
| PUT | `/api/wind/{id}` | Update a turbine |
| DELETE | `/api/wind/{id}` | Deactivate a turbine |
| POST | `/api/wind/{id}/output` | Record hourly output |

### Battery Service — `:8083`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/battery` | Register a battery |
| GET | `/api/battery` | List all batteries |
| GET | `/api/battery/{id}` | Get a battery |
| PUT | `/api/battery/{id}` | Update a battery |
| DELETE | `/api/battery/{id}` | Deactivate a battery |
| POST | `/api/battery/{id}/charge` | Add energy (capped at 100%) |
| POST | `/api/battery/{id}/discharge` | Remove energy (floored at 0%) |

### Energy Distribution Service — `:8084`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/distribution/process` | Run distribution for a demand value |
| GET | `/api/distribution/{id}` | Get a distribution record |
| GET | `/api/distribution` | List all distributions |
| POST | `/api/distribution/faults/detect` | Detect and persist faults |
| GET | `/api/distribution/faults` | Get full fault history |
| GET | `/api/distribution/report/daily` | Daily report (`?date=YYYY-MM-DD`, defaults to today) |

---

## Example Requests

### Register a solar panel

```
POST http://localhost:8081/api/solar
Content-Type: application/json

{
  "deviceName": "Panel-A1",
  "location": "Rooftop-North",
  "capacity": 500.0,
  "status": "ACTIVE",
  "maintenance": "OPERATIONAL"
}
```

### Charge a battery

```
POST http://localhost:8083/api/battery/1/charge
Content-Type: application/json

{ "energyAmountKwh": 25.0 }
```

### Process distribution

```
POST http://localhost:8084/api/distribution/process
Content-Type: application/json

{ "demandKwh": 450.0 }
```

Response:

```json
{
  "id": 1,
  "requestedDemand": 450.0,
  "renewablePower": 500.0,
  "batteryPower": -50.0,
  "distributedPower": 450.0,
  "remainingPower": 0.0,
  "distributionDate": "2026-07-21T09:05:00"
}
```

**`batteryPower` sign convention:**
- Negative → excess renewable energy was stored into the battery
- Positive → battery discharged to cover a shortfall

### Detect faults

```
POST http://localhost:8084/api/distribution/faults/detect
```

Response:

```json
[
  {
    "id": 1,
    "deviceType": "SOLAR_PANEL",
    "deviceId": 3,
    "faultType": "ZERO_GENERATION",
    "description": "Solar panel Panel-C3 produced zero generation during active hours",
    "createdAt": "2026-07-21T12:00:00"
  }
]
```

---

## Distribution Logic

1. Aggregate current renewable generation: `renewablePower = sum(solar.generation) + sum(wind.output)` for ACTIVE, OPERATIONAL devices.
2. Compare to `requestedDemand`:
   - **Excess** (`renewablePower > demand`): charge batteries up to 100% until excess is absorbed. `batteryPower` is negative.
   - **Shortfall** (`renewablePower < demand`): discharge batteries to close the gap. `batteryPower` is positive.
   - **Any remaining unmet demand** → recorded as `remainingPower`.

## Fault Detection

Runs across all three device services and flags:

- **Zero Generation** — solar panel produced 0 output during active hours
- **Zero Output** — wind turbine produced 0 output during active hours
- **Low Battery** — battery charge below 20% threshold
- **Under Maintenance** — equipment flagged as `UNDER_MAINTENANCE` (excluded from generation)

---

## Swagger UI

Once each service is running:

| Service | URL |
|---|---|
| Solar | http://localhost:8081/swagger-ui.html |
| Wind | http://localhost:8082/swagger-ui.html |
| Battery | http://localhost:8083/swagger-ui.html |
| Distribution | http://localhost:8084/swagger-ui.html |

---

## Postman Collection

Import `postman/Renewable-Energy-Monitoring-System.postman_collection.json`.

Includes all endpoints, organized into folders by service, with a fifth folder showing gateway-routed equivalents. Collection variables are pre-set to the default local ports — no editing needed.

---

## Database

One MySQL schema per service (database-per-service pattern). No cross-database foreign keys. Cross-service relationships are resolved at the application layer.

| Schema | Tables |
|---|---|
| `solar_db` | `solar_panels` |
| `wind_db` | `wind_turbines` |
| `battery_db` | `batteries` |
| `distribution_db` | `distributions`, `faults` |

Full schema and indexes: see `sql/ER_DIAGRAM.md`.

---

## Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| `ExceptionInInitializerError ... TypeTag :: UNKNOWN` | Lombok incompatible with very recent JDK (24+) | Use JDK 17 or 21, or invalidate IntelliJ caches |
| `Unable to acquire JDBC Connection` | MySQL not running or wrong credentials | Check MySQL on 3306, update `application.properties` |
| Distribution returns 500 | Device services not running | Start solar, wind, battery first |
| Gateway returns 404 | Wrong path or target down | Check `/actuator/gateway/routes`, ensure target service is up |

---

## Future Enhancements

- Replace RestTemplate with **WebClient** for non-blocking calls
- Add **Eureka/Consul** for dynamic service discovery
- Add **Resilience4j** circuit breakers
- Move fault detection to a `@Scheduled` background job
- Add **Spring Security / OAuth2** at the gateway
- Publish events to **Kafka/RabbitMQ** for analytics
- Use **Flyway/Liquibase** for schema migrations
- Add **Docker Compose** for one-command local startup
