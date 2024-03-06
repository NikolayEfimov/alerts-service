package app.domain;

public class SensorMetricsResponse {
    private int maxLast30Days;
    private int avgLast30Days;

    public SensorMetricsResponse(int maxLast30Days, int avgLast30Days) {
        this.maxLast30Days = maxLast30Days;
        this.avgLast30Days = avgLast30Days;
    }

    public int getMaxLast30Days() {
        return maxLast30Days;
    }

    public void setMaxLast30Days(int maxLast30Days) {
        this.maxLast30Days = maxLast30Days;
    }

    public double getAvgLast30Days() {
        return avgLast30Days;
    }

    public void setAvgLast30Days(int avgLast30Days) {
        this.avgLast30Days = avgLast30Days;
    }
}
