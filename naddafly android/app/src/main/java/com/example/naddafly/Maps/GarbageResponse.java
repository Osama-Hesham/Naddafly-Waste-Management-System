package com.example.naddafly.Maps;

import com.google.gson.annotations.SerializedName;

public class GarbageResponse {
    @SerializedName("id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("volume")
    private String volume;

    public GarbageResponse(double latitude, double longitude, String size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.volume = size;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSize() {
        return volume;
    }

    public void setSize(String size) {
        this.volume = size;
    }
}
