package org.mnilsen.weather.core;

public class Station {
    private int id;
    private String  name;
    private String  description;


    public Station(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Station)) {
            return false;
        }
        final Station other = (Station) object;
        if (id != other.id) {
            return false;
        }
        if (!(description == null ? other.description == null : description.equals(other.description))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + id;
        result = PRIME * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }
}
