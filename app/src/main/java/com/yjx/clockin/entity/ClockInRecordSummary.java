package com.yjx.clockin.entity;

public class ClockInRecordSummary {
    private Integer id;
    private String day;
    private double monthManHours;
    private double manHours;
    private double averageManHours;

    private int dayCount;

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public double getNeedRepairHours() {
        return this.dayCount * 8.5 - this.monthManHours;
    }
    public double getMonthManHours() {
        return monthManHours;
    }

    public void setMonthManHours(double monthManHours) {
        this.monthManHours = monthManHours;
    }

    public double getManHours() {
        return manHours;
    }

    public void setManHours(double manHours) {
        this.manHours = manHours;
    }

    public double getAverageManHours() {
        return averageManHours;
    }

    public void setAverageManHours(double averageManHours) {
        this.averageManHours = averageManHours;
    }

    public ClockInRecordSummary() {
    }
}
