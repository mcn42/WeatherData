package org.mnilsen.weather.logger;

import java.util.HashMap;
import java.util.Map;

public class AppProperties {
    private static final String DB_HOST = "dbHost";
    private static final String DB_PORT = "dbPort";
    private static final String MEASUREMENT_PERIOD_MILLIS = "measurementPeriodMillis";
    private static final String MEASUREMENT_DB_NAME = "measurementDBName";
    private static final String MEASUREMENT_COLLECTION_NAME = "measurementCollectionName";
    private static final String COLLECTOR_ID = "collectorId";
    

    private Map<String,Object> properties = new HashMap<>();
    
    public AppProperties() {
        super();
        init();
    }
    
    private void init() {
        properties.put(DB_HOST, "localhost");
        properties.put(DB_PORT, 27017);
        properties.put(MEASUREMENT_PERIOD_MILLIS, 60 * 1000L);
        properties.put(MEASUREMENT_DB_NAME, "weather");
        properties.put(MEASUREMENT_COLLECTION_NAME, "observations");
        properties.put(COLLECTOR_ID, "1");
    }
    
    public String getCollectorId() {
        return (String) this.properties.get(COLLECTOR_ID);
    }
    
    public String getDbHost() {
        return (String) this.properties.get(DB_HOST);
    }
    
    public Integer getDbPort() {
        return (Integer) this.properties.get(DB_PORT);
    }
    
    public Long getMeasurementPeriodMillis() {
        return (Long) this.properties.get(MEASUREMENT_PERIOD_MILLIS);
    }
    
    public String getMeasurementDBName() {
        return (String) this.properties.get(MEASUREMENT_DB_NAME);
    }
    
    public String getMeasurementCollectionName() {
        return (String) this.properties.get(MEASUREMENT_COLLECTION_NAME);
    }
}
