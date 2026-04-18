package com.example.hotellapp.models;

public class RoomType {
    private int id;
    private String typeName;
    private String description;
    private int capacity;
    private double pricePerNight;
    private String thumbnailUrl;

    public RoomType(int id, String typeName, int capacity, double pricePerNight, String thumbnailUrl) {
        this.id = id;
        this.typeName = typeName;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getId() { return id; }
    public String getTypeName() { return typeName; }
    public int getCapacity() { return capacity; }
    public double getPricePerNight() { return pricePerNight; }
    public String getThumbnailUrl() { return thumbnailUrl; }

    @Override
    public String toString() {
        return typeName;
    }
}
