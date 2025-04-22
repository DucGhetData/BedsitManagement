package com.example.rentinghousemanagement.model;

public class ChiTietHopDong {
    private int contractId;
    private String tenantName;
    private String idNumber;
    private int roomId;
    private double price;
    private double square;
    private String startDate;
    private String endDate;
    private double depositAmount;
    private int status;
    private String cancelDate;
    private int cancelParty;
    private double compenAmount;

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSquare() {
        return square;
    }

    public void setSquare(double square) {
        this.square = square;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public int getCancelParty() {
        return cancelParty;
    }

    public void setCancelParty(int cancelParty) {
        this.cancelParty = cancelParty;
    }

    public double getCompenAmount() {
        return compenAmount;
    }

    public void setCompenAmount(double compenAmount) {
        this.compenAmount = compenAmount;
    }
}
