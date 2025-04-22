package com.example.rentinghousemanagement.model;

public class Khach {
    private int tenantId;
    private String tenantName;
    private String phoneNumber;
    private String idNumber;
    private int roomId;
    private String birthDate;
    private String endDate;

    public Khach(int tenantId, String tenantName, String phoneNumber, String idNumber, int roomId, String birthDate, String endDate) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.phoneNumber = phoneNumber;
        this.idNumber = idNumber;
        this.roomId = roomId;
        this.birthDate = birthDate;
        this.endDate = endDate;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
