package com.doanh.testggmaps;

public class Cinema implements Comparable<Cinema> {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String detailAddress;
    private String manager;
    private float distance;

    public Cinema(int id, String name, double latitude, double longitude, String detailAddress, String manager) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.detailAddress = detailAddress;
        this.manager = manager;
    }

    public Cinema(String name, double latitude, double longitude, String detailAddress, String manager) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.detailAddress = detailAddress;
        this.manager = manager;
        id = hashCode();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Cinema o) {
        return Float.compare(this.distance, o.distance);
    }
}