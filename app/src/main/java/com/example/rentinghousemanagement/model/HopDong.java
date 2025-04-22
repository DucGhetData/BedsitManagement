package com.example.rentinghousemanagement.model;

public class HopDong {
    private int id;
    private String customerName;
    private String startDate;
    private String endDate;
    private TinhTrangHopDong status;
    private double depositAmount;

    public HopDong(int id, String customerName, String startDate, String endDate, TinhTrangHopDong status, double depositAmount) {
        this.id = id;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.depositAmount = depositAmount;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public TinhTrangHopDong getStatus() {
        return status;
    }

    public void setStatus(TinhTrangHopDong status) {
        this.status = status;
    }


}
