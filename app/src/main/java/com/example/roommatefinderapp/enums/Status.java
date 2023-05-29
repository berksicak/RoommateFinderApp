package com.example.roommatefinderapp.enums;

public enum Status {
    NEEDHOME("Kalacak Ev/Oda arıyor"),
    NEEDFRIEND("Ev/Oda arkadaşı arıyor"),
    NOTHING("Aramıyor");

    private final String status;
    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
