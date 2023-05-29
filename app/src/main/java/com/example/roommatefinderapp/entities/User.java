package com.example.roommatefinderapp.entities;

import android.graphics.Bitmap;

import com.example.roommatefinderapp.enums.Status;
import com.example.roommatefinderapp.enums.Year;

import java.time.LocalDate;

public class User {
    private String userID;
    private String name;
    private String surname;
    private String email;
    private Bitmap image;
    private String phoneNo;
    private Year year;
    private String department;
    private Status status;
    private int distanceToCampus;
    private String duration;

    public User(String userID, String name, String surname, String phoneNo, Year year, String department, Status status, int distanceToCampus, String duration) {
        this.userID = userID;
        this.name = name;
        this.surname = surname;
        this.phoneNo = phoneNo;
        this.year = year;
        this.department = department;
        this.status = status;
        this.distanceToCampus = distanceToCampus;
        this.duration = duration;
    }


    public User(String id, String name, String surname, String email, Bitmap image) {
        this.userID = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.image = image;
    }


    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getDistanceToCampus() {
        return distanceToCampus;
    }

    public void setDistanceToCampus(int distanceToCampus) {
        this.distanceToCampus = distanceToCampus;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}

