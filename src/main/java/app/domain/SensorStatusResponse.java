package app.domain;

public class SensorStatusResponse {
    private SensorStatus status;

    public SensorStatusResponse(SensorStatus status) {
        this.status = status;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }
}
