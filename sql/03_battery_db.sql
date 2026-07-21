-- ===================================================================
-- BATTERY DB - battery-service
-- ===================================================================
CREATE DATABASE IF NOT EXISTS battery_db;
USE battery_db;

CREATE TABLE IF NOT EXISTS batteries (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name          VARCHAR(255) NOT NULL,
    location             VARCHAR(255) NOT NULL,
    capacity             DOUBLE NOT NULL,
    charge_percentage    DOUBLE NOT NULL DEFAULT 0,
    available_capacity   DOUBLE,
    remaining_capacity   DOUBLE,
    status               VARCHAR(20) NOT NULL,
    created_at           DATETIME NOT NULL,

    CONSTRAINT chk_battery_capacity_positive CHECK (capacity > 0),
    CONSTRAINT chk_battery_charge_range CHECK (charge_percentage >= 0 AND charge_percentage <= 100),
    CONSTRAINT chk_battery_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'FAULT', 'LOW_BATTERY'))
);

CREATE INDEX idx_battery_status ON batteries (status);
CREATE INDEX idx_battery_charge ON batteries (charge_percentage);
