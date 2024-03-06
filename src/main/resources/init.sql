CREATE SCHEMA IF NOT EXISTS sensor_data;

CREATE TABLE IF NOT EXISTS sensor_data (
                                                       id UUID PRIMARY KEY,
                                                       status VARCHAR(255)
    );

CREATE SCHEMA IF NOT EXISTS measurements;

CREATE TABLE IF NOT EXISTS measurement (
                                                        id SERIAL PRIMARY KEY,
                                                        sensor_id UUID,
                                                        value INT,
                                                        timestamp TIMESTAMP,
                                                        FOREIGN KEY (sensor_id) REFERENCES sensor_data.sensor_data(id)
    );

CREATE SCHEMA IF NOT EXISTS alerts;

CREATE TABLE IF NOT EXISTS alert (
                                            id SERIAL PRIMARY KEY,
                                            sensor_id UUID,
                                            start_time TIMESTAMP,
                                            finish_time TIMESTAMP,
                                            FOREIGN KEY (sensor_id) REFERENCES sensor_data.sensor_data(id)
    );

CREATE TABLE IF NOT EXISTS alert_measurement (
                                                 alert_id BIGINT,
                                                 measurement_id BIGINT,
                                                 FOREIGN KEY (alert_id) REFERENCES alerts.alert(id),
                                                 FOREIGN KEY (measurement_id) REFERENCES measurements.measurement(id),
                                                 PRIMARY KEY (alert_id, measurement_id)
);

CREATE INDEX IF NOT EXISTS idx_measurement_sensor_id ON measurements.measurement(sensor_id);
CREATE INDEX IF NOT EXISTS idx_measurement_timestamp ON measurements.measurement(timestamp);
CREATE INDEX IF NOT EXISTS idx_alert_sensor_id ON alerts.alert(sensor_id);
CREATE INDEX IF NOT EXISTS idx_alert_start_time ON alerts.alert(start_time);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA sensor_data TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA measurements TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA alerts TO postgres;
