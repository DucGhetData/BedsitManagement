package com.example.rentinghousemanagement.model;

import java.io.Serializable;

public class DenBu implements Serializable {
    private int compensatioId;
    private int roomId;
    private String createDate;
    private int tenantId;
    private String tenantName;
    private String phone_number;
    private Double total_amount;

    public DenBu(int compensatioId, int roomId, String createDate, int tenantId, String tenantName, String phone_number, Double total_amount) {
        this.compensatioId = compensatioId;
        this.roomId = roomId;
        this.createDate = createDate;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.phone_number = phone_number;
        this.total_amount = total_amount;
    }

    public int getCompensatioId() {
        return compensatioId;
    }

    public void setCompensatioId(int compensatioId) {
        this.compensatioId = compensatioId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }
}
