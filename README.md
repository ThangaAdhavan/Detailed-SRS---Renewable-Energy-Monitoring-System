# 🌱 Renewable Energy Monitoring System

## Overview
Renewable Energy Monitoring System is a Spring Boot microservices application that manages solar panels, wind turbines, battery storage, and renewable energy distribution. It follows a distributed microservices architecture where each domain has its own database and is exposed through an API Gateway.

## Architecture

Client -> API Gateway -> Solar Service / Wind Service / Battery Service / Energy Distribution Service

The Distribution Service communicates with the other services using RestTemplate to aggregate renewable generation, coordinate battery charging/discharging, detect faults, and generate reports.

## Microservices

| Service | Port | Database |
|---|---:|---|
| API Gateway | 8080 | - |
| Solar Panel Service | 8081 | solar_db |
| Wind Turbine Service | 8082 | wind_db |
| Battery Service | 8083 | battery_db |
| Energy Distribution Service | 8084 | distribution_db |

## Features

### Solar Panel Service
- CRUD operations
- Hourly generation recording
- Zero-generation fault lookup

### Wind Turbine Service
- CRUD operations
- Hourly power recording
- Zero-output fault lookup

### Battery Service
- CRUD operations
- Charge/Discharge batteries
- Capacity tracking
- Low battery alerts

### Energy Distribution Service
- Aggregate renewable generation
- Renewable-first distribution
- Battery charging/discharging
- Daily reports
- Fault detection

### API Gateway
- Centralized routing
- CORS support

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Cloud Gateway
- Spring Data JPA
- MySQL
- Maven
- Lombok
- Swagger/OpenAPI
- RestTemplate
- JUnit 5
- Mockito

## Project Structure

```
RenewableEnergyMonitoringSystem/
├── api-gateway/
├── solar-panel-service/
├── wind-turbine-service/
├── battery-service/
├── energy-distribution-service/
├── sql/
└── postman/
```

## Setup

1. Install Java 17, Maven and MySQL.
2. Execute SQL scripts in the sql folder.
3. Update application.properties for database credentials.
4. Start services:
```bash
cd solar-panel-service && mvn spring-boot:run
cd wind-turbine-service && mvn spring-boot:run
cd battery-service && mvn spring-boot:run
cd energy-distribution-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## Business Flow

1. Solar panels and wind turbines submit generation data.
2. Distribution Service fetches generation from Solar and Wind services.
3. Renewable energy satisfies demand first.
4. Excess energy charges batteries.
5. Battery energy is discharged if renewable generation is insufficient.
6. Fault detection identifies zero output, low battery, and maintenance devices.

## Swagger

- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html
- http://localhost:8084/swagger-ui.html

## Future Enhancements

- WebClient
- Eureka
- Resilience4j
- Kafka/RabbitMQ
- Spring Security
- Docker
- Kubernetes
- Flyway

## Author

Developed as a Spring Boot Microservices project for renewable energy monitoring.
