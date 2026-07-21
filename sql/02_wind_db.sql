-- ===================================================================
-- WIND DB - wind-turbine-service
-- ===================================================================
CREATE DATABASE IF NOT EXISTS wind_db;
USE wind_db;

CREATE TABLE IF NOT EXISTS wind_turbines (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name         VARCHAR(255) NOT NULL,
    location            VARCHAR(255) NOT NULL,
    capacity            DOUBLE NOT NULL,
    current_output      DOUBLE DEFAULT 0,
    status              VARCHAR(20) NOT NULL,
    maintenance         VARCHAR(30) NOT NULL,
    created_at          DATETIME NOT NULL,

    CONSTRAINT chk_wind_capacity_positive CHECK (capacity > 0),
    CONSTRAINT chk_wind_output_nonnegative CHECK (current_output >= 0),
    CONSTRAINT chk_wind_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'FAULT')),
    CONSTRAINT chk_wind_maintenance CHECK (maintenance IN ('OPERATIONAL', 'UNDER_MAINTENANCE'))
);

CREATE INDEX idx_wind_status ON wind_turbines (status);
CREATE INDEX idx_wind_maintenance ON wind_turbines (maintenance);
CREATE INDEX idx_wind_location ON wind_turbines (location);
