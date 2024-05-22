package com.yjx.clockin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yjx.clockin.R;
import com.yjx.clockin.adapter.DailyClockInAdapter;
import com.yjx.clockin.constants.ClockInConstants;
import com.yjx.clockin.database.ClockInDao;
import com.yjx.clockin.database.DatabaseHelper;
import com.yjx.clockin.entity.ClockInDate;
import com.yjx.clockin.entity.ClockInRecord;
import com.yjx.clockin.entity.ClockInRecordSummary;
import com.yjx.clockin.entity.DailyClockInRecord;
import com.yjx.clockin.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClockInRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private DailyClockInAdapter dailyClockInAdapter;

    private DatabaseHelper databaseHelper;

    private ClockInDao clockInDao;

    private TextView todayManHoursText;
    private TextView averageManHoursText;
    private TextView monthManHoursText;
    private TextView btnAddClockIn;
    private LinearLayout addClockInView;
    private ClockInRecordSummary todaySummary;
    private ImageView backIndex;
    private RelativeLayout btnDeleteAdd;
    private RelativeLayout btnSubmitAdd;

    private TextView addClockInDay;
    private TextView addClockInFirst;
    private TextView addClockInLast;

    private ClockInDate clockInFirstDate;
    private ClockInDate clockInLastDate;
    private TextView needRepairHoursText;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.d("ClockInRecordActivity", "handleMessage");
                    //完成主界面更新,拿到数据
                    updateDisplaySummary();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_record);

        initData();
        initView();
        listView = findViewById(R.id.daily_clock_in_list);
        refreshRecord();
    }

    private void refreshRecord() {
        List<DailyClockInRecord> dailyClockInRecords = getDailyClockInRecords();
        dailyClockInAdapter = new DailyClockInAdapter(this, dailyClockInRecords, clockInDao, mHandler);
        listView.setAdapter(dailyClockInAdapter);
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this,"ClockIn.db",null,3);
        clockInDao = new ClockInDao(databaseHelper);
        clockInFirstDate = new ClockInDate();
        clockInLastDate = new ClockInDate();
    }
    private void initView() {
        updateDisplaySummary();
        backIndex = findViewById(R.id.back_index);
        backIndex.setOnClickListener(this);

        btnAddClockIn = findViewById(R.id.btn_add_clock_in);
        btnAddClockIn.setOnClickListener(this);

        addClockInView = findViewById(R.id.add_clock_in);

        btnDeleteAdd = findViewById(R.id.btn_delete_add);
        btnDeleteAdd.setOnClickListener(this);

        btnSubmitAdd = findViewById(R.id.btn_submit_add);
        btnSubmitAdd.setOnClickListener(this);

        addClockInDay = findViewById(R.id.add_clock_in_day);
        addClockInDay.setOnClickListener(this);

        addClockInFirst = findViewById(R.id.add_clock_in_first);
        addClockInFirst.setOnClickListener(this);

        addClockInLast = findViewById(R.id.add_clock_in_last);
        addClockInLast.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_clock_in_day) {
            showDatePickerDialog();
        }
        if (v.getId() == R.id.add_clock_in_first) {
            showTimePickerDialog(addClockInFirst, clockInFirstDate);
        }
        if (v.getId() == R.id.add_clock_in_last) {
            showTimePickerDialog(addClockInLast, clockInLastDate);
        }
        if (v.getId() == R.id.btn_delete_add) {
            addClockInView.setVisibility(View.GONE);
        }
        if (v.getId() == R.id.btn_submit_add) {
            addClockInRecord();
            addClockInView.setVisibility(View.GONE);
            refreshRecord();
            updateDisplaySummary();
        }
        if (v.getId() == R.id.btn_add_clock_in) {
            if (addClockInView.getVisibility() == View.VISIBLE) {
                addClockInView.setVisibility(View.GONE);
            } else {
                addClockInView.setVisibility(View.VISIBLE);
            }
            addClockInDay.setText("");
            addClockInFirst.setText("");
            addClockInLast.setText("");
        }
        if (v.getId() == R.id.back_index) {
            Intent intent = new Intent(ClockInRecordActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void addClockInRecord() {
        try {
            String day = DateUtils.getDateString(clockInFirstDate.getDate(), ClockInConstants.DATE_FORMAT_YMD);
            Cursor cursor = clockInDao.querySummary(day, ClockInConstants.COLUMN_NAME_DAY);
            if (cursor.getCount() != 0) {
                Toast.makeText(this, "["+ day +"]已存在记录！请删除记录后重试！", Toast.LENGTH_SHORT).show();
                return;
            }
            Date firstDate = clockInFirstDate.getDate();
            Date lastDate = clockInLastDate.getDate();
            clockInDao.recordClockInInfo(firstDate);
            clockInDao.recordClockInInfo(lastDate);
            Toast.makeText(this, "补打卡成功！", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Toast.makeText(this, "补打卡失败，请稍后重试！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 处理选择的日期（例如：更新UI显示）
                        String day = year + "-"+ DateUtils.appendZeroForDate(monthOfYear + 1)
                                + "-" + DateUtils.appendZeroForDate(dayOfMonth);
                        addClockInDay.setText(day);
                        clockInFirstDate.setYear(year);
                        clockInFirstDate.setMonth(monthOfYear + 1);
                        clockInFirstDate.setDay(dayOfMonth);
                        clockInLastDate.setYear(year);
                        clockInLastDate.setMonth(monthOfYear + 1);
                        clockInLastDate.setDay(dayOfMonth);
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
        datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
    private void showTimePickerDialog(TextView addClockInTime, ClockInDate clockInDate) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 处理选择的时间（例如：更新UI显示）
                        String time = DateUtils.appendZeroForDate(hourOfDay) + ":"
                                + DateUtils.appendZeroForDate(minute);
                        addClockInTime.setText(time);
                        clockInDate.setHour(hourOfDay);
                        clockInDate.setMinute(minute);
                    }
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
        timePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        timePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }


    private void updateDisplaySummary() {
        todaySummary = clockInDao.getSummary();
        todayManHoursText = findViewById(R.id.today_man_hours);
        monthManHoursText = findViewById(R.id.month_man_hours);
        averageManHoursText = findViewById(R.id.average_man_hours);
        needRepairHoursText = findViewById(R.id.need_repair_hours);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00"); // 设置保留一位小数的格式
        String manHours = decimalFormat.format(todaySummary.getManHours());
        String averageManHours = decimalFormat.format(todaySummary.getAverageManHours());
        String monthManHours = decimalFormat.format(todaySummary.getMonthManHours());
        todayManHoursText.setText(manHours);
        monthManHoursText.setText(monthManHours);
        averageManHoursText.setText(averageManHours);
        int greenColor = Color.parseColor("#65CB00");
        if (todaySummary.getManHours() > 8) {
            todayManHoursText.setTextColor(greenColor);
        }
        String needRepairHours = decimalFormat.format(Math.abs(todaySummary.getNeedRepairHours()));
        needRepairHoursText.setText(needRepairHours);
        if (todaySummary.getAverageManHours() > 8.5) {
            averageManHoursText.setTextColor(greenColor);
        } else {
            needRepairHoursText.setTextColor(Color.RED);
        }
    }

    @SuppressLint("Range")
    private List<DailyClockInRecord> getDailyClockInRecords(){
        List<DailyClockInRecord> dailyClockInRecords = new ArrayList<>();
        Date date = new Date();
        String month = DateUtils.getDateString(date, ClockInConstants.DATE_FORMAT_YM);
        Cursor cursor = clockInDao.queryRecord(month, ClockInConstants.COLUMN_NAME_MONTH);
        List<ClockInRecord> clockInRecords = new ArrayList<>();
        List<String> days = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                String dbMonth = cursor.getString(cursor.getColumnIndex(ClockInConstants.COLUMN_NAME_MONTH));
                String day = cursor.getString(cursor.getColumnIndex(ClockInConstants.COLUMN_NAME_DAY));
                String clockInTime = cursor.getString(cursor.getColumnIndex(ClockInConstants.COLUMN_NAME_CLOCK_IN_TIME));
                Log.d("MainActivity", "first clock in is (" + dbMonth + "," + day + "," + clockInTime +")");
                clockInRecords.add(new ClockInRecord(dbMonth, day, clockInTime));
                if (!days.contains(day)) {
                    days.add(day);
                }
            } while (cursor.moveToNext());
        }
        Map<String, List<ClockInRecord>> dayToRecordMap = clockInRecords.stream().collect(Collectors.groupingBy(ClockInRecord::getDay));
        for (int i = days.size() - 1; i >= 0; i--) {
            List<ClockInRecord> records = dayToRecordMap.get(days.get(i));
            DailyClockInRecord dailyClockInRecord = new DailyClockInRecord(days.get(i), records.get(0).getClockInTime(), records.get(records.size() - 1).getClockInTime());
            float hoursBetween;
            try {
                Date firstDate = DateUtils.getDateByString(records.get(0).getClockInTime(), ClockInConstants.DATE_FORMAT_YMD_HMS);
                Date lastDate = DateUtils.getDateByString(records.get(records.size() - 1).getClockInTime(), ClockInConstants.DATE_FORMAT_YMD_HMS);
                hoursBetween = clockInDao.getWorkingHoursBetween(firstDate, lastDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            dailyClockInRecord.setDayHours(hoursBetween);
            dailyClockInRecords.add(dailyClockInRecord);
        }
        return dailyClockInRecords;
    }

}