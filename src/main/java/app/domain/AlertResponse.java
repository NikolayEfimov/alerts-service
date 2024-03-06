package app.domain;

import java.time.LocalDateTime;

public class AlertResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int measurement1;
    private int measurement2;
    private int measurement3;

    public AlertResponse(LocalDateTime startTime, LocalDateTime endTime, int measurement1, int measurement2, int measurement3) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.measurement1 = measurement1;
        this.measurement2 = measurement2;
        this.measurement3 = measurement3;
    }

    public AlertResponse() {
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getMeasurement1() {
        return measurement1;
    }

    public void setMeasurement1(int measurement1) {
        this.measurement1 = measurement1;
    }

    public int getMeasurement2() {
        return measurement2;
    }

    public void setMeasurement2(int measurement2) {
        this.measurement2 = measurement2;
    }

    public int getMeasurement3() {
        return measurement3;
    }

    public void setMeasurement3(int measurement3) {
        this.measurement3 = measurement3;
    }
}

