package com.example.rentinghousemanagement.model;

public class Phong {
    private int roomId;
    private int userId;
    private float square;
    private float price;
    private int capacity;
    private int status;

    public Phong(int roomId, int userId, float square, float price, int capacity, int status) {
        this.roomId = roomId;
        this.userId = userId;
        this.square = square;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getSquare() {
        return square;
    }

    public void setSquare(float square) {
        this.square = square;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
