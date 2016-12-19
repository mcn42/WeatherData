package org.mnilsen.weather.core;

public class Reading {
    private Station station;
    private long timestamp;
    private double tempC;
    private double tempF;
    private double pressure;
    private double humidity;

    public Reading(Station station, long timestamp, double tempC, double tempF, double pressure, double humidity) {
        this.station = station;
        this.timestamp = timestamp;
        this.tempC = tempC;
        this.tempF = tempF;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public Station getStation() {
        return station;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getTempC() {
        return tempC;
    }

    public double getTempF() {
        return tempF;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Reading)) {
            return false;
        }
        final Reading other = (Reading) object;
        if (!(station == null ? other.station == null : station.equals(other.station))) {
            return false;
        }
        if (timestamp != other.timestamp) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((station == null) ? 0 : station.hashCode());
        result = PRIME * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
