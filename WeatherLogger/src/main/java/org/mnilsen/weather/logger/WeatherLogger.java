package org.mnilsen.weather.logger;


import com.mongodb.DB;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.io.IOException;

import java.util.Timer;

import java.util.TimerTask;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import org.mnilsen.weather.core.Reading;
import org.mnilsen.weather.core.Utils;

public class WeatherLogger {
    private AppProperties properties = new AppProperties();
    private Bme280Device device = new Bme280Device();
    private Timer t = new Timer();
    private MeasurementTask task = new MeasurementTask();
    private MongoClient mongoClient = null;
    private MongoDatabase mongoDb = null;
    private MongoCollection mongoColl = null;
    private Logger logger = Utils.getLogger();
    
    public WeatherLogger() {
        super();
    }
    
    public void start() {
        logger.info(String.format("Starting WeatherLogger '%s'",this.properties.getCollectorId()));
        try {
            mongoClient = new MongoClient(this.properties.getDbHost(), this.properties.getDbPort());
            this.mongoDb = this.mongoClient.getDatabase(this.properties.getMeasurementDBName());
            this.mongoColl = this.mongoDb.getCollection(this.properties.getMeasurementCollectionName());
        } catch (Exception e) {
            logger.log(Level.SEVERE,String.format("MongoDB init error in WeatherLogger '%s'",this.properties.getCollectorId()),e);
            return;
        }
        this.task = new MeasurementTask();
        this.t.schedule(task, 10 * 1000L, this.properties.getMeasurementPeriodMillis());
    }
    
    public void stop() {
        logger.info(String.format("Stopping WeatherLogger '%s'",this.properties.getCollectorId()));
        
        if(this.task != null) this.task.cancel();
        if(this.mongoClient != null) this.mongoClient.close();       
        this.task = null;
    }

    public static void main(String[] args) {
        WeatherLogger weatherLogger = new WeatherLogger();
        weatherLogger.start();
    }
    
    private class MeasurementTask extends TimerTask {

        @Override
        public void run() {
            logger.info(String.format("WeatherLogger '%s' initiating measurement",properties.getCollectorId()));
            Reading rd = null;
            try {
                rd = device.takeReading();
            } catch (IOException | UnsupportedBusNumberException e) {
                logger.log(Level.SEVERE,String.format("Weather data read error in WeatherLogger '%s'",properties.getCollectorId()),e);
                return;
            }
            if(rd == null) {
                logger.severe("Reading was NULL");
                return;
            }
            logger.info(String.format("Data retrieved: %s",Utils.getReadingJson(rd)));
            
            try {
                Document doc = Document.parse(Utils.getReadingJson(rd));
                mongoColl.insertOne(doc);
            } catch (Exception e) {
                logger.log(Level.SEVERE,String.format("MongoDB insert error in WeatherLogger '%s'",properties.getCollectorId()),e);
            }
        }
    }
}
