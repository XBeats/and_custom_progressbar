package com.aitangba.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by XBeats on 2016/9/24.
 */
public class CustomDrawView extends View {

    private int mLineWidth = 50;

    public CustomDrawView(Context context) {
        super(context);
    }

    public CustomDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private int mDistance =  2 * mLineWidth; //px 0 <--> 2 * mLineWidth
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = getMeasuredHeight();

        final int startPointX = 100;
        final int endPointX = 400;

        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);

        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        canvas.drawColor(Color.WHITE);

        Rect clipAreaRect = new Rect(startPointX, 0, endPointX, height);
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        canvas.drawRect(clipAreaRect, redPaint);

        int angle = 45;
        float radian = (float) Math.toRadians(angle);
        float radianTanValue = (float) Math.tan(radian);
        int offsetX = mDistance > mLineWidth ?  -2 * mLineWidth + mDistance  : mDistance; //offsetX
        float paddingDistance = height * radianTanValue; //paddingDistance
        final int rectWidth = endPointX - startPointX; //rectWidth
        final float totalWidth = offsetX + Math.abs(paddingDistance) + rectWidth;
        final int cycleCount = (int) Math.ceil((double) totalWidth / mLineWidth);

        final float drawStartX = (paddingDistance > 0 ? startPointX - paddingDistance : startPointX)
                + (offsetX);
        final Path rectPath = new Path();
        rectPath.addRect(drawStartX, 0, drawStartX + mLineWidth, height, Path.Direction.CW);

        Path deepPath = new Path();
        deepPath.moveTo(drawStartX, 0);
        for(int i = 0 ; i < cycleCount; i ++) {
            deepPath.addPath(rectPath, i * 2 * mLineWidth, 0);
        }

        Rect deepAreaRect = new Rect(startPointX, 0, endPointX, height);
        canvas.clipRect(deepAreaRect);
        canvas.skew(radianTanValue, 0);

        canvas.drawPath(deepPath, yellowPaint);
    }
}
