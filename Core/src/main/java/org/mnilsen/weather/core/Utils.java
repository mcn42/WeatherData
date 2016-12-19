package org.mnilsen.weather.core;

import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.JSONObject;

public class Utils {
    private static Logger logger = Logger.getLogger("org.mnilsen.weather");
    
    static {
        configureLog();
    }
    
    private Utils() {
        super();
    }

    private static void configureLog() {
        try {
            FileHandler fh = new FileHandler("./Weather_%g.log",100000,6,true);
            fh.setFormatter(new SimpleFormatter());      
            fh.setLevel(Level.ALL);
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Failed to add logging FileHandler",e);
        }
    //        try {
    //            RestLogHandler rlh = new RestLogHandler("./Announcements_%g.log");
    //
    //            rlh.setLevel(Level.INFO);
    //            logger.addHandler(rlh);
    //        } catch (Exception e) {
    //            logger.log(Level.SEVERE,"Failed to add logging RetLogHandler",e);
    //        }
    }
    
    public static Logger getLogger() {
        return logger;
    }
    
    public static String getReadingJson(Reading rd) {
        JSONObject jobj = new JSONObject();
        jobj.append("timestamp", rd.getTimestamp());
        jobj.append("station", rd.getStation().getId());
        jobj.append("tempC", rd.getTempC());
        jobj.append("tempF", rd.getTempF());
        jobj.append("humidity", rd.getHumidity());
        jobj.append("pressure", rd.getPressure());
        return jobj.toString();
    }
    
    public static Reading getReadingFromJson(String json) {
        JSONObject jobj = new JSONObject(json);
        long timestamp = jobj.getLong("timestamp");
        int id = jobj.getInt("station");
        double tempC = jobj.getDouble("tempC");
        double tempF = jobj.getDouble("tempF");
        double humidity = jobj.getDouble("humidity");
        double pressure = jobj.getDouble("pressure");
        Station sta = Stations.getStationForId(id);
        return new Reading(sta,timestamp,tempC,tempF,pressure,humidity);
    }
}
