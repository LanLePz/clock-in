package com.yjx.clockin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjx.clockin.R;
import com.yjx.clockin.constants.ClockInConstants;
import com.yjx.clockin.database.ClockInDao;
import com.yjx.clockin.database.DatabaseHelper;
import com.yjx.clockin.entity.ClockInRecordSummary;
import com.yjx.clockin.utils.DateUtils;
import com.yjx.clockin.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private CircleImageView clockInButton;

    private CircleImageView clockInLoadingButton;

    private LinearLayout indexInfoCard;
    private ClockInRecordSummary todaySummary;

    private TextView todayManHoursText;
    private TextView averageManHoursText;
    private TextView monthManHoursText;
    private TextView clockInTips;
    private TextView lastClockInTime;
    private TextView clockInHistory;

    private TextView needRepairHoursText;

    private ClockInDao clockInDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        databaseHelper = new DatabaseHelper(this,"ClockIn.db",null,3);
        clockInDao = new ClockInDao(databaseHelper);
    }


    private void initView() {
        clockInButton = findViewById(R.id.clock_in);
        clockInLoadingButton = findViewById(R.id.clock_in_loading);
        clockInTips = findViewById(R.id.clock_in_tips);
        lastClockInTime = findViewById(R.id.last_clock_in_time);
        clockInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date nowDate = new Date();
                clockInLoadingButton.setVisibility(View.VISIBLE);
                Animation clockInAni = AnimationUtils.loadAnimation(MainActivity.this,R.anim.loading_rotate);
                clockInLoadingButton.startAnimation(clockInAni);
                clockInAni.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clockInLoadingButton.setVisibility(View.GONE);
                        clockInTips.setText("已打卡");
                        lastClockInTime.setText(DateUtils.getDateString(nowDate, ClockInConstants.DATE_FORMAT_YMD_HMS));
                        lastClockInTime.setVisibility(View.VISIBLE);
                        clockInDao.recordClockInInfo(nowDate);
                        updateDisplaySummary();
                        ToastUtils.toastClockIn(MainActivity.this, "记得打卡哦~", Color.RED, 21);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
        indexInfoCard = findViewById(R.id.index_info_card);
        Animation project_ani = AnimationUtils.loadAnimation(this,R.anim.translate_indexinfo);
        indexInfoCard.setAnimation(project_ani);
        updateDisplaySummary();

        clockInHistory = findViewById(R.id.clock_in_history);
        clockInHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClockInRecordActivity.class);
                startActivity(intent);
            }
        });
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


    @Override
    protected void onDestroy() {
        //页面销毁等场景，主动关闭数据库链接
        databaseHelper.close();
        super.onDestroy();
    }
}