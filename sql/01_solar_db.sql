-- ===================================================================
-- SOLAR DB - solar-panel-service
-- ===================================================================
CREATE DATABASE IF NOT EXISTS solar_db;
USE solar_db;

CREATE TABLE IF NOT EXISTS solar_panels (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name         VARCHAR(255) NOT NULL,
    location            VARCHAR(255) NOT NULL,
    capacity            DOUBLE NOT NULL,
    current_generation  DOUBLE DEFAULT 0,
    status              VARCHAR(20) NOT NULL,
    maintenance         VARCHAR(30) NOT NULL,
    created_at          DATETIME NOT NULL,

    CONSTRAINT chk_solar_capacity_positive CHECK (capacity > 0),
    CONSTRAINT chk_solar_generation_nonnegative CHECK (current_generation >= 0),
    CONSTRAINT chk_solar_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'FAULT')),
    CONSTRAINT chk_solar_maintenance CHECK (maintenance IN ('OPERATIONAL', 'UNDER_MAINTENANCE'))
);

CREATE INDEX idx_solar_status ON solar_panels (status);
CREATE INDEX idx_solar_maintenance ON solar_panels (maintenance);
CREATE INDEX idx_solar_location ON solar_panels (location);
