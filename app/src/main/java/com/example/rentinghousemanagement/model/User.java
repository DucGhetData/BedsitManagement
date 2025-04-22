package com.example.rentinghousemanagement.model;

public class User {
    private int userId;
    private String userName, email, phoneNumber, password;

    public User(int userId, String userName, String email, String phoneNumber, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
}

