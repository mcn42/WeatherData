package org.mnilsen.weather.display;

import com.mongodb.Mongo;

import java.util.concurrent.atomic.AtomicReference;

public class WeatherDisplay {
    private Mongo mongo = null;
    private String dbUrl = "";
    
    private long displayPeriod = 15000L;
    
    private AtomicReference<Double> currentTemp = new AtomicReference<>();
    private AtomicReference<Double> currentHumidity = new AtomicReference<>();
    private AtomicReference<Double> currentPressure = new AtomicReference<>();
    
    private AtomicReference<Double> highTemp = new AtomicReference<>();
    private AtomicReference<Double> highHumidity = new AtomicReference<>();
    private AtomicReference<Double> highPressure = new AtomicReference<>();
    
    private AtomicReference<Double> lowTemp = new AtomicReference<>();
    private AtomicReference<Double> lowHumidity = new AtomicReference<>();
    private AtomicReference<Double> lowPressure = new AtomicReference<>();
    
    private AtomicReference<Double> avgTemp = new AtomicReference<>();
    private AtomicReference<Double> avgHumidity = new AtomicReference<>();
    private AtomicReference<Double> avgPressure = new AtomicReference<>();
    
    public WeatherDisplay() {
        super();
    }

    public static void main(String[] args) {
        WeatherDisplay weatherDisplay = new WeatherDisplay();
    }
    
    public void start() {
        this.start(15000L,"");
    }
    
    public void start(long displayPeriod,String dbUrl) {
        this.displayPeriod =  displayPeriod;
        this.dbUrl = dbUrl;
    }
    
    private void retrieveData() {
        this.retrieveAverages();
        this.retrieveLows();
        this.retrieveHighs();
        this.retrieveCurrent();
    }
    
    private void retrieveAverages() {
        
    }
    
    private void retrieveHighs() {
        
    }
    
    private void retrieveLows() {
        
    }
    
    private void retrieveCurrent() {
        
    }
    
    public void shutdown() {
        
    }
    
    private long getOneMonthAgo() {
        return 0L;
    }
}
