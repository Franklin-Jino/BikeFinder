package com.example.bikefinderapp;

import java.io.Serializable;

public class Bike  implements Serializable {
    private String chaiseNo;
    private String latitude;
    private String longitude;
    private String accuracy;

    public Bike(String chaiseNo, String latitude, String longitude, String accuracy) {
        this.chaiseNo= chaiseNo;
        this.latitude=latitude;
        this.longitude=longitude;
        this.accuracy=accuracy;
    }

    public Bike() {
    }

    public String getChaiseNo() {
        return chaiseNo;
    }

    public void setChaiseNo(String chaiseNo) {
        this.chaiseNo = chaiseNo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
