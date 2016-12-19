package org.mnilsen.weather.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Stations {
    private static Map<Integer,Station> stationsById = new HashMap<>();
    
    static {
        Station s = new Station(1,"Test","First test station");
        stationsById.put(s.getId(), s);
    }
    
    private Stations() {
        super();
    }
    
    public static Station getStationForId(Integer id) {
        return stationsById.get(id);
    }
    
    public static Collection<Station> getAllStations() {
        return stationsById.values();
    }
}
