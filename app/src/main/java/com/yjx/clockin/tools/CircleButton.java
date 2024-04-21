package com.yjx.clockin.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleButton extends View {
    Paint paint;
    public CircleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //实例化画笔对象
        paint = new Paint();
        //设置颜色
        paint.setColor(Color.parseColor("#65CB00"));
        //设置画笔属性
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawCircle(getWidth()/2,getHeight()/2,200,paint);
    }
}
