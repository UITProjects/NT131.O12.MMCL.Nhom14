package com.uit.sensordht.Model;

public class WeatherData {
    private long timestamp;
    private float temperature;

    public WeatherData(long timestamp, float temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    // Getters and setters if needed

    // You might want to override toString() for easy printing or use in logs
    @Override
    public String toString() {
        return "WeatherData{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                '}';
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
