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
        int offsetX = mDistance > mLineWidth ? mDistance - 2 * mLineWidth : mDistance;
        float radianTanValue = (float) Math.tan(Math.toRadians(angle));
        float offsetDistance = height * radianTanValue;
        float totalWidth = endPointX - startPointX + Math.abs(offsetDistance);
        final int cycleCount = (int) Math.ceil((double) totalWidth / mLineWidth);
        final float drawStartX = (offsetDistance > 0 ? startPointX - offsetDistance : startPointX) + offsetX;

        Path deepPath = new Path();
        deepPath.moveTo(drawStartX, 0);
        for(int i = 0 ; i < cycleCount; i ++) {
            final float horizontalStartX = drawStartX + mLineWidth * i;
            final float horizontalEndX = horizontalStartX + mLineWidth;
            final int lineHeight = i % 2 == 0 ? height : 0;

            //draw vertical
            deepPath.lineTo(horizontalStartX, lineHeight);
            //draw horizontal
            deepPath.lineTo(horizontalEndX, lineHeight);
        }
        //last line
        if(cycleCount % 2 != 0) {
            float horizontalX = drawStartX + mLineWidth * cycleCount;
            deepPath.lineTo(horizontalX, 0);
        }
        deepPath.close();

        Rect deepAreaRect = new Rect(startPointX, 0, endPointX, height);
        canvas.clipRect(deepAreaRect);
        canvas.skew(radianTanValue, 0);

        canvas.drawPath(deepPath, yellowPaint);
    }
}
