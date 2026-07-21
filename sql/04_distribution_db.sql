-- ===================================================================
-- DISTRIBUTION DB - energy-distribution-service
-- ===================================================================
CREATE DATABASE IF NOT EXISTS distribution_db;
USE distribution_db;

CREATE TABLE IF NOT EXISTS distributions (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    renewable_power    DOUBLE NOT NULL,
    battery_power      DOUBLE NOT NULL,
    distributed_power  DOUBLE NOT NULL,
    remaining_power    DOUBLE NOT NULL,
    requested_demand   DOUBLE NOT NULL,
    distribution_date  DATETIME NOT NULL,

    CONSTRAINT chk_distribution_demand_positive CHECK (requested_demand > 0),
    CONSTRAINT chk_distribution_remaining_nonnegative CHECK (remaining_power >= 0)
);

CREATE TABLE IF NOT EXISTS faults (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_type   VARCHAR(20) NOT NULL,
    device_id     BIGINT,
    fault_type    VARCHAR(30) NOT NULL,
    description   VARCHAR(500) NOT NULL,
    created_at    DATETIME NOT NULL,

    CONSTRAINT chk_fault_device_type CHECK (device_type IN ('SOLAR_PANEL', 'WIND_TURBINE', 'BATTERY')),
    CONSTRAINT chk_fault_type CHECK (fault_type IN ('ZERO_GENERATION', 'LOW_BATTERY', 'UNDER_MAINTENANCE'))
);

CREATE INDEX idx_distribution_date ON distributions (distribution_date);
CREATE INDEX idx_fault_device_type ON faults (device_type);
CREATE INDEX idx_fault_type ON faults (fault_type);
CREATE INDEX idx_fault_created_at ON faults (created_at);

-- Note: distributions and faults reference devices (solar panels, wind turbines,
-- batteries) that live in OTHER services' databases (solar_db, wind_db, battery_db).
-- Because each microservice owns its own database (database-per-service pattern),
-- device_id is a *logical* foreign key only -- it is NOT enforced with a physical
-- FOREIGN KEY constraint across databases. Referential integrity for these
-- cross-service references is maintained at the application layer via the
-- RestTemplate calls in energy-distribution-service.
