package com.yjx.clockin.entity;

public class DailyClockInRecord {
    private String day;
    private String firstClockInTime;
    private String lastClockInTime;

    private double dayHours;

    public double getDayHours() {
        return dayHours;
    }

    public void setDayHours(double dayHours) {
        this.dayHours = dayHours;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFirstClockInTime() {
        return firstClockInTime;
    }

    public void setFirstClockInTime(String firstClockInTime) {
        this.firstClockInTime = firstClockInTime;
    }

    public String getLastClockInTime() {
        return lastClockInTime;
    }

    public void setLastClockInTime(String lastClockInTime) {
        this.lastClockInTime = lastClockInTime;
    }

    public DailyClockInRecord(String day, String firstClockInTime, String lastClockInTime) {
        this.day = day;
        this.firstClockInTime = firstClockInTime;
        this.lastClockInTime = lastClockInTime;
    }
}
