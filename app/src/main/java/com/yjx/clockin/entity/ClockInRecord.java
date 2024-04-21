package com.yjx.clockin.entity;

public class ClockInRecord {
    private String day;
    private String month;
    private String clockInTime;

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public ClockInRecord() {
    }

    public ClockInRecord(String month, String day, String clockInTime) {
        this.month = month;
        this.day = day;
        this.clockInTime = clockInTime;
    }
}
