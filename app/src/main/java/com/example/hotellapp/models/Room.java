package com.example.hotellapp.models;

public class Room {
    private final int roomId;
    private final int roomTypeId;
    private final String roomNumber;
    private final int floorNumber;
    private final String roomStatus;
    private final String typeName;
    private final String bedType;
    private final double pricePerNight;
    private final double sizeSqm;
    private final int capacity;
    private final String amenities;
    private final String thumbnailUrl;

    public Room(int roomId, int roomTypeId, String roomNumber, int floorNumber, String roomStatus,
                String typeName, String bedType, double pricePerNight, double sizeSqm,
                int capacity, String amenities, String thumbnailUrl) {
        this.roomId = roomId;
        this.roomTypeId = roomTypeId;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
        this.roomStatus = roomStatus;
        this.typeName = typeName;
        this.bedType = bedType;
        this.pricePerNight = pricePerNight;
        this.sizeSqm = sizeSqm;
        this.capacity = capacity;
        this.amenities = amenities;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getRoomId() { return roomId; }
    public int getRoomTypeId() { return roomTypeId; }
    public String getRoomNumber() { return roomNumber; }
    public int getFloorNumber() { return floorNumber; }
    public String getRoomStatus() { return roomStatus; }
    public String getTypeName() { return typeName; }
    public String getBedType() { return bedType; }
    public double getPricePerNight() { return pricePerNight; }
    public double getSizeSqm() { return sizeSqm; }
    public int getCapacity() { return capacity; }
    public String getAmenities() { return amenities; }
    public String getThumbnailUrl() { return thumbnailUrl; }
}
