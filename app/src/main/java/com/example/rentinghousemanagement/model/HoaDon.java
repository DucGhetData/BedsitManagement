package com.example.rentinghousemanagement.model;

import java.io.Serializable;

public class HoaDon implements Serializable {
    private String invoiceId;
    private String monthYear;
    private int roomId;
    private int tenantId;
    private String tenantName;
    private String phoneNumber;
    private double roomPrice;
    private double totalAmount;

    public HoaDon(String invoiceId, String monthYear, int roomId, int tenantId, String tenantName, String phoneNumber, double roomPrice, double totalAmount) {
        this.invoiceId = invoiceId;
        this.monthYear = monthYear;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.phoneNumber = phoneNumber;
        this.roomPrice = roomPrice;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
