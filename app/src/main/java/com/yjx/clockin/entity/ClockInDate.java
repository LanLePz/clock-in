package com.yjx.clockin.entity;

import com.yjx.clockin.constants.ClockInConstants;
import com.yjx.clockin.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class ClockInDate {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public ClockInDate(int year, int month, int day, int hour, int minute) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }



    public ClockInDate() {

    }

    public Date getDate() throws ParseException {
            String dateStr = year + "-"+ DateUtils.appendZeroForDate(month)
                    + "-" + DateUtils.appendZeroForDate(day) + " "
                    + DateUtils.appendZeroForDate(hour) + ":"
                    + DateUtils.appendZeroForDate(minute) + ":00";
            return DateUtils.getDateByString(dateStr, ClockInConstants.DATE_FORMAT_YMD_HMS);
    }


}