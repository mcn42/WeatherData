package org.mnilsen.weather.display;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherDisplay {
    private Timer t = new Timer();
    private DisplayTask dTask = new DisplayTask();
    private RetrieveTask rTask = new RetrieveTask();
    private MongoClient mongoClient = null;
    private MongoDatabase mongoDb = null;
    private MongoCollection mongoColl = null;
    private Logger logger = Logger.getLogger("name");
    private DisplayMode displayMode = DisplayMode.CURRENT;
    private String dbHost = "";
    private int dbPort = 0;

    private long displayPeriod = 15000L;
    private long retrievePeriod = 300000L;

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
        this.start(15000L,300000L, "");
    }

    public void start(long displayPeriod, long retrievePeriod,String dbUrl) {
        this.displayPeriod = displayPeriod;
        this.retrievePeriod = retrievePeriod;
        this.dbHost = dbUrl;

        logger.info(String.format("Starting Weather Display '%s'", ""));
        try {
            mongoClient = new MongoClient();
            this.mongoDb = this.mongoClient.getDatabase("");
            this.mongoColl = this.mongoDb.getCollection("");
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                       String.format("MongoDB init error in WeatherLogger '%s'", ""), e);
            return;
        }
        
        this.rTask = new RetrieveTask();
        this.t.schedule(this.dTask, 0L, this.retrievePeriod);
        this.dTask = new DisplayTask();
        this.t.schedule(this.dTask, 0L, this.displayPeriod);
    }

    private void retrieveData() {
        this.retrieveAverages();
        this.retrieveLows();
        this.retrieveHighs();
        this.retrieveCurrent();
    }
    
    private void changeDisplay() {
        switch(this.displayMode) {
            case CURRENT:
            this.displayCurrent();
            break;
        
            case AVG:
            this.displayAverages();
            break;
        
            case HIGH:
            this.displayHighs();
            break;
        
            case LOW:
            this.displayLows();
            break;                             
        }
        this.displayMode = this.displayMode.getNext();
    }
    
    private void retrieveAverages() {

    }

    private void retrieveHighs() {

    }

    private void retrieveLows() {

    }

    private void retrieveCurrent() {

    }

    private void displayAverages() {

    }

    private void displayHighs() {

    }

    private void displayLows() {

    }

    private void displayCurrent() {

    }

    public void shutdown() {

    }

    private long getOneMonthAgo() {
        return 0L;
    }

    private class DisplayTask extends TimerTask {
        @Override
        public void run() {
            changeDisplay();
        }
    }

    private class RetrieveTask extends TimerTask {
        @Override
        public void run() {
            retrieveData();
        }
    }
}
