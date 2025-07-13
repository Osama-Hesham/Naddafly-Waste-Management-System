package com.example.naddafly.Maps;

public class GarbageItem {
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
    private double latitude;
    private double longitude;
    private String distance;
    private String duration;
    private String size;
    private  int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GarbageItem(double latitude, double longitude, String distance, String duration, String size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.duration = duration;
        this.size = size;
    }

    public GarbageItem(double latitude, double longitude, String distance, String duration, String size , String imageUrl , int id ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.duration = duration;
        this.size = size;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getSize() {
        return size;
    }
}
