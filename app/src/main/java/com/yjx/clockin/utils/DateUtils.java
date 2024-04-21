package com.yjx.clockin.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static String getDateString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //将字符串转为date对象
        return sdf.format(date);
    }

    public static Date getDateByString(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //将字符串转为date对象
        return sdf.parse(date);
    }

    public static String appendZeroForDate(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return String.valueOf(time);
    }
}
