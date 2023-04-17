package com.example.bouncermodule;

public class Bars {
    public String lineLength;
    public Integer lineCount;
    public Double longitude;
    public Double latitude;

    public Bars() {
        // Default constructor required for calls to DataSnapshot.getValue(Bars.class)
    }

    public Bars(String lineLength, Integer lineCount, Double longitude, Double latitude) {
        this.lineLength = lineLength;
        this.lineCount = lineCount;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLineLength(String lineLength){
        this.lineLength = lineLength;
    }

    public String getLineLength(){
        return this.lineLength;
    }

    public void setLineCount(Integer lineCount){
        this.lineCount = lineCount;
    }

    public Integer getLineCount(){
        return this.lineCount;
    }

    public Double getLatitude(){
        return this.latitude;
    }

    public Double getLongitude(){
        return this.longitude;
    }
}
