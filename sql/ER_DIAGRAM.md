# Entity Relationship Diagram

Each microservice owns an independent MySQL schema (database-per-service
pattern). There are no physical cross-database foreign keys; relationships
between devices and distribution/fault records are logical and are resolved
at the application layer through RestTemplate calls between services.

```mermaid
erDiagram
    SOLAR_PANELS {
        BIGINT id PK
        VARCHAR device_name
        VARCHAR location
        DOUBLE capacity
        DOUBLE current_generation
        VARCHAR status
        VARCHAR maintenance
        DATETIME created_at
    }

    WIND_TURBINES {
        BIGINT id PK
        VARCHAR device_name
        VARCHAR location
        DOUBLE capacity
        DOUBLE current_output
        VARCHAR status
        VARCHAR maintenance
        DATETIME created_at
    }

    BATTERIES {
        BIGINT id PK
        VARCHAR device_name
        VARCHAR location
        DOUBLE capacity
        DOUBLE charge_percentage
        DOUBLE available_capacity
        DOUBLE remaining_capacity
        VARCHAR status
        DATETIME created_at
    }

    DISTRIBUTIONS {
        BIGINT id PK
        DOUBLE requested_demand
        DOUBLE renewable_power
        DOUBLE battery_power
        DOUBLE distributed_power
        DOUBLE remaining_power
        DATETIME distribution_date
    }

    FAULTS {
        BIGINT id PK
        VARCHAR device_type
        BIGINT device_id "logical FK -> SOLAR_PANELS.id / WIND_TURBINES.id / BATTERIES.id"
        VARCHAR fault_type
        VARCHAR description
        DATETIME created_at
    }

    SOLAR_PANELS ||--o{ FAULTS : "reports (logical, cross-service)"
    WIND_TURBINES ||--o{ FAULTS : "reports (logical, cross-service)"
    BATTERIES ||--o{ FAULTS : "reports (logical, cross-service)"
    SOLAR_PANELS }o--o{ DISTRIBUTIONS : "contributes generation to (logical, via REST aggregation)"
    WIND_TURBINES }o--o{ DISTRIBUTIONS : "contributes generation to (logical, via REST aggregation)"
    BATTERIES }o--o{ DISTRIBUTIONS : "charged/discharged by (logical, via REST call)"
```

## Relationship Notes

| Relationship | Type | Enforcement |
|---|---|---|
| `faults.device_id` -> `solar_panels.id` / `wind_turbines.id` / `batteries.id` | Logical FK, disambiguated by `faults.device_type` | Application layer (energy-distribution-service calls the owning service before recording a fault) |
| `distributions` aggregates power from solar + wind + battery | Many-to-many, computed | Application layer (RestTemplate calls at request time, not persisted as a join) |

## Primary Keys
- `solar_panels.id`, `wind_turbines.id`, `batteries.id`, `distributions.id`, `faults.id` — all `BIGINT AUTO_INCREMENT`.

## Indexes
- `solar_panels(status)`, `solar_panels(maintenance)`, `solar_panels(location)`
- `wind_turbines(status)`, `wind_turbines(maintenance)`, `wind_turbines(location)`
- `batteries(status)`, `batteries(charge_percentage)`
- `distributions(distribution_date)`
- `faults(device_type, device_id)`, `faults(created_at)`
