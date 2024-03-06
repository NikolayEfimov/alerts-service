package app.domain;

import java.time.LocalDateTime;

public class MeasurementRequest {

    private int co2;
    private LocalDateTime time;

    public MeasurementRequest() {}

    public MeasurementRequest(int co2, LocalDateTime time) {
        this.co2 = co2;
        this.time = time;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
