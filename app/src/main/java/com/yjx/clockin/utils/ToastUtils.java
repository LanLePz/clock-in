package com.yjx.clockin.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    public static void toastClockIn(Context context, String text, int color, int textSize) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        LinearLayout layout = new LinearLayout(context);
        TextView textView=new TextView(context);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(textSize);
        layout.addView(textView);
        toast.setView(layout);
        toast.show();
    }
}
