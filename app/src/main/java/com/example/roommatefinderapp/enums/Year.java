package com.example.roommatefinderapp.enums;

public enum Year {
    PREP("Hazırlık"),
    FIRST("1. Sınıf"),
    SECOND("2. Sınıf"),
    THIRD("3. Sınıf"),
    SENIOR("4. Sınıf");

    private final String year;

    Year(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }
}
