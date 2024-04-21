package com.yjx.clockin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yjx.clockin.R;
import com.yjx.clockin.constants.ClockInConstants;
import com.yjx.clockin.database.ClockInDao;
import com.yjx.clockin.entity.DailyClockInRecord;
import com.yjx.clockin.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.List;

public class DailyClockInAdapter extends BaseAdapter {
    private Context context;
    private List<DailyClockInRecord> dailyClockInRecords;

    private ClockInDao clockInDao;

    private boolean isChanged = false;
    Handler mHandler;
    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        this.isChanged = changed;
    }

    public DailyClockInAdapter(Context context, List<DailyClockInRecord> dailyClockInRecords,
                               ClockInDao clockInDao, Handler mHandler) {
        this.context = context;
        this.dailyClockInRecords = dailyClockInRecords;
        this.clockInDao = clockInDao;
        this.mHandler = mHandler;
    }

    @Override
    public int getCount() {
        return dailyClockInRecords.size();
    }

    @Override
    public DailyClockInRecord getItem(int position) {
        return dailyClockInRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_daily_clock_in, null);
            vh = new ListViewHolder();
            vh.day = convertView.findViewById(R.id.daily_clock_in_day);
            vh.first = convertView.findViewById(R.id.daily_clock_in_first);
            vh.last = convertView.findViewById(R.id.daily_clock_in_last);
            vh.dayHours = convertView.findViewById(R.id.daily_clock_in_day_hours);

            convertView.setTag(vh);
        }else {
            vh = (ListViewHolder) convertView.getTag();
        }

        DailyClockInRecord dailyClockInRecord = dailyClockInRecords.get(position);
        vh.day.setText(dailyClockInRecord.getDay());
        vh.first.setText(dailyClockInRecord.getFirstClockInTime());
        vh.last.setText(dailyClockInRecord.getLastClockInTime());
        DecimalFormat decimalFormat = new DecimalFormat("#0.00"); // 设置保留一位小数的格式
        String manHours = decimalFormat.format(dailyClockInRecord.getDayHours());
        vh.dayHours.setText(manHours);
        if (dailyClockInRecord.getDayHours() > 8.5) {
            int greenColor = Color.parseColor("#65CB00");
            vh.dayHours.setTextColor(greenColor);
        }
        vh.delete = convertView.findViewById(R.id.delete_button);
        vh.delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(context, "已删除["+ dailyClockInRecord.getDay() +"]打卡记录！", Toast.LENGTH_SHORT).show();
                clockInDao.deleteClockInRecord(dailyClockInRecord.getDay(), ClockInConstants.COLUMN_NAME_DAY);
                dailyClockInRecords.remove(position);
                setChanged(true);
                notifyDataSetChanged();
                mHandler.sendEmptyMessage(0);
            }
        });
        return convertView;
    }

    private final class ListViewHolder {
        public TextView day;
        public TextView first;
        public TextView last;
        public TextView dayHours;

        public View delete;
    }
}
