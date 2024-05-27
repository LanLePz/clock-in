package com.yjx.clockin.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yjx.clockin.constants.ClockInConstants;
import com.yjx.clockin.entity.ClockInRecordSummary;
import com.yjx.clockin.entity.DailyClockInRecord;
import com.yjx.clockin.utils.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ClockInDao {
    private SQLiteDatabase dbReader;

    private SQLiteDatabase dbWriter;

    public ClockInDao(DatabaseHelper databaseHelper) {
        this.dbReader = databaseHelper.getReadableDatabase();
        this.dbWriter = databaseHelper.getWritableDatabase();
    }

    public Cursor queryRecord(String queryDay, String columnName) {
        String[] projection = {
                ClockInConstants.COLUMN_NAME_ID,
                ClockInConstants.COLUMN_NAME_MONTH,
                ClockInConstants.COLUMN_NAME_DAY,
                ClockInConstants.COLUMN_NAME_CLOCK_IN_TIME
        };

        String selection = columnName + " = ?";
        String[] selectionArgs = {queryDay};
        //按照某个列排序
        String sortOrder = ClockInConstants.COLUMN_NAME_CLOCK_IN_TIME + " ASC";
        return dbReader.query(ClockInConstants.TABLE_NAME_CLOCK_IN_RECORD, projection, selection, selectionArgs, null,null, sortOrder);
    }

    public ClockInRecordSummary getSummary(Date date){
        ClockInRecordSummary summary = new ClockInRecordSummary();
        getManHours(date, ClockInConstants.COLUMN_NAME_DAY, summary);
        getManHours(date, ClockInConstants.COLUMN_NAME_MONTH, summary);
        return summary;
    }

    @SuppressLint("Range")
    @Nullable
    private void getManHours(Date date, String columnName, ClockInRecordSummary summary) {
        String format = ClockInConstants.COLUMN_NAME_DAY.equals(columnName)
                ? ClockInConstants.DATE_FORMAT_YMD : ClockInConstants.DATE_FORMAT_YM;
        boolean isDay = ClockInConstants.COLUMN_NAME_DAY.equals(columnName);
        String nowMonthOrDay = DateUtils.getDateString(date, format);
        Cursor dayCursor = querySummary(nowMonthOrDay, columnName);
        float manHours = 0F;
        if (dayCursor.getCount() == 0) {
            summary.setManHours(manHours);
            return;
        }
        try {
            if (dayCursor.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印
                    int id = dayCursor.getInt(dayCursor.getColumnIndex(ClockInConstants.COLUMN_NAME_ID));
                    String month = dayCursor.getString(dayCursor.getColumnIndex(ClockInConstants.COLUMN_NAME_MONTH));
                    String day = dayCursor.getString(dayCursor.getColumnIndex(ClockInConstants.COLUMN_NAME_DAY));
                    float dayManHours = dayCursor.getFloat(dayCursor.getColumnIndex(ClockInConstants.COLUMN_NAME_MAN_HOURS));
                    Log.d("MainActivity", "clock in summary is (" + id + "," + month + "," + day + "," + dayManHours + ")");
                    if (isDay) {
                        Log.d("MainActivity", "is day !!!");
                        summary.setManHours(dayManHours);
                        return;
                    }
                    manHours += dayManHours;
                } while (dayCursor.moveToNext());
                summary.setMonthManHours(manHours);
                summary.setAverageManHours(manHours / dayCursor.getCount());
                summary.setDayCount(dayCursor.getCount());
                Log.d("MainActivity", "summary is (" + summary.getMonthManHours() + "," + summary.getAverageManHours() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dayCursor.close();
        }
    }

    public void recordClockInInfo(Date nowDate) {
        try {
            ContentValues values = new ContentValues();
            //将字符串转为date对象
            String clockInTime = DateUtils.getDateString(nowDate, ClockInConstants.DATE_FORMAT_YMD_HMS);
            String day = DateUtils.getDateString(nowDate, ClockInConstants.DATE_FORMAT_YMD);
            String month = DateUtils.getDateString(nowDate, ClockInConstants.DATE_FORMAT_YM);

            // 开始组装第一条数据
            values.put(ClockInConstants.COLUMN_NAME_MONTH, month);
            values.put(ClockInConstants.COLUMN_NAME_DAY, day);
            values.put(ClockInConstants.COLUMN_NAME_CLOCK_IN_TIME, clockInTime);
            dbWriter.insert(ClockInConstants.TABLE_NAME_CLOCK_IN_RECORD, null, values); // 插入第一条数据
            Log.d("MainActivity", "clock in info is (" + day + "," + clockInTime + ")");
            calculateManHours(nowDate, day);
        } catch (Exception e) {
            Log.i("recordClockInInfo()", "failed to record clock in info");
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    private void calculateManHours(Date nowDate, String nowDay) {
        Cursor cursor = queryRecord(nowDay, ClockInConstants.COLUMN_NAME_DAY);
        if (cursor.getCount() == 1) {
            return;
        }
        try {
            if (cursor.moveToFirst()) {
                // 遍历Cursor对象，取出数据并打印
                String day = cursor.getString(cursor.getColumnIndex(ClockInConstants.COLUMN_NAME_DAY));
                String clockInTime = cursor.getString(cursor.getColumnIndex(ClockInConstants.COLUMN_NAME_CLOCK_IN_TIME));
                Date clockDate = DateUtils.getDateByString(clockInTime, ClockInConstants.DATE_FORMAT_YMD_HMS);
                //计算两个日期间隔
                float hoursBetween = getWorkingHoursBetween(clockDate, nowDate);
                Log.d("MainActivity", "calculateManHours is (" + day + "," + clockInTime + ","
                        + DateUtils.getDateString(nowDate, ClockInConstants.DATE_FORMAT_YMD_HMS) + "," + hoursBetween +")");
                updateDailyManHours(day, hoursBetween);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public float getWorkingHoursBetween(Date firstDate, Date lastDate) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(lastDate);
        int firstHour = firstCalendar.get(Calendar.HOUR_OF_DAY);
        int firstMin = firstCalendar.get(Calendar.MINUTE);
        int lastHour = lastCalendar.get(Calendar.HOUR_OF_DAY);
        int lastMin = lastCalendar.get(Calendar.MINUTE);
        float[] workingHours = getWorkingHours();
        float betweenHours = 0;
        for (int i = 0; i < workingHours.length; i++) {
            if (i + 1 < firstHour) {
                workingHours[i] = 0;
            }
            if (firstHour == i + 1 && firstHour >= 8) {
                if(firstHour == 12) {
                    workingHours[i] = 0;
                } else if (firstHour == 13) {
                    workingHours[i] = firstMin >= 30 ? (60 - firstMin) / 60f : 0.5f;
                } else if (firstHour == 17) {
                    workingHours[i] = firstMin < 30 ? (30 - firstMin) / 60f : 0f;
                } else {
                    workingHours[i] = (60 - firstMin) / 60f;
                }
            }
            if (lastHour == i + 1 && lastHour >= 8) {
                if(lastHour == 12) {
                    workingHours[i] = 0;
                } else if (lastHour == 13) {
                    workingHours[i] = lastMin >= 30 ? (lastMin - 30) / 60f : 0;
                } else if (lastHour == 17) {
                    workingHours[i] = lastMin < 30 ? lastMin / 60f : 0.5f;
                } else {
                    workingHours[i] = lastMin / 60f;
                }
            } else if (lastHour < i + 1) {
                workingHours[i] = 0;
            }
            if (firstHour == i + 1 && lastHour == i + 1) {
                workingHours[i] = (lastMin - firstMin) / 60f;
            }
            betweenHours += workingHours[i];
        }
        return betweenHours;
    }
    private float[] getWorkingHours() {
        float[] workingHours = new float[24];
        for (int i = 0; i < workingHours.length; i++) {
            if (i < 7) {
                workingHours[i] = 0;
            }else {
                workingHours[i] = 1;
            }
        }
        workingHours[11] = 0;
        workingHours[12] = 0.5f;
        workingHours[16] = 0.5f;
        return workingHours;
    }

    private void updateDailyManHours(String day, float hoursBetween) {
        Cursor cursor = querySummary(day, ClockInConstants.COLUMN_NAME_DAY);
        if (cursor.getCount() == 0) {
            try {
                Date date = DateUtils.getDateByString(day, ClockInConstants.DATE_FORMAT_YMD);
                String month = DateUtils.getDateString(date, ClockInConstants.DATE_FORMAT_YM);
                Log.d("MainActivity", "insertSummary : month is (" + month + ")");
                insertSummary(month, day, hoursBetween);
                return;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        updateSummary(day, hoursBetween);
    }

    private void updateSummary(String day, float hoursBetween) {
        ContentValues values = new ContentValues();
        // 开始组装第一条数据
        values.put(ClockInConstants.COLUMN_NAME_MAN_HOURS, hoursBetween);
        String selection = ClockInConstants.COLUMN_NAME_DAY + " = ?";
        String[] selectionArgs = {day};
        dbWriter.update(ClockInConstants.TABLE_NAME_CLOCK_IN_SUMMARY, values, selection, selectionArgs);
    }

    private void insertSummary(String month, String day, float hoursBetween) {
        ContentValues values = new ContentValues();
        // 开始组装第一条数据
        values.put(ClockInConstants.COLUMN_NAME_MONTH, month);
        values.put(ClockInConstants.COLUMN_NAME_DAY, day);
        values.put(ClockInConstants.COLUMN_NAME_MAN_HOURS, hoursBetween);
        dbWriter.insert(ClockInConstants.TABLE_NAME_CLOCK_IN_SUMMARY, null, values); // 插入第一条数据
    }

    public Cursor querySummary(String queryMonthOrDay, String columnName) {
        Log.d("MainActivity", "querySummary : param is (" + queryMonthOrDay + " , " + columnName + ")");
        String[] projection = {
                ClockInConstants.COLUMN_NAME_ID,
                ClockInConstants.COLUMN_NAME_MONTH,
                ClockInConstants.COLUMN_NAME_DAY,
                ClockInConstants.COLUMN_NAME_MAN_HOURS
        };

        String selection = columnName + " = ?";
        String[] selectionArgs = {queryMonthOrDay};
        return dbReader.query(ClockInConstants.TABLE_NAME_CLOCK_IN_SUMMARY, projection, selection, selectionArgs, null,null,null);
    }

    public void deleteClockInRecord(String dayOrMonth, String columnName) {
        Log.d("MainActivity", "deleteClockInRecord : param is (" + dayOrMonth + ")");
        String selection = columnName + " = ?";
        String[] selectionArgs = {dayOrMonth};
        dbReader.delete(ClockInConstants.TABLE_NAME_CLOCK_IN_RECORD, selection, selectionArgs);
        dbReader.delete(ClockInConstants.TABLE_NAME_CLOCK_IN_SUMMARY, selection, selectionArgs);
    }
}
