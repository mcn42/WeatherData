package org.mnilsen.weather.display;


public enum DisplayMode {
    CURRENT,HIGH,LOW,AVG;    

    public DisplayMode getNext() {
        DisplayMode m = null;
        if(this.ordinal() + 1 == DisplayMode.values().length) {
            m = DisplayMode.values()[0];
        } else {
            m = DisplayMode.values()[this.ordinal() + 1];
        }
        return m;
    }   
    
}
