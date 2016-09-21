package com.aitangba.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by fhf11991 on 2016/9/20.
 */
public class ProgressbarView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int REFRESH_TIME = 70; //ms

    private SurfaceHolder holder;

    private Paint mLightPaint;
    private Paint mDeepPaint;
    private Paint mShadePaint;

    private int mDeepColorId = Color.parseColor("#91E13D");
    private int mLightColorId = Color.parseColor("#B9EC83");
    private int mBackgroundColor = Color.WHITE;

    private boolean mIsPlaying;
    private int mDistance; //px 0 <--> 2 * mLineWidth
    private int mLineWidth = 20; //px
    private double mAngle = 60; //
    private double mProgress = (double) 3  / 4;

    /**
     * between 0 to 100
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = (double) progress / 100;
    }

    /**
     * between 0 to 90
     * @param angle
     */
    public void setAngle(double angle) {
        mAngle = angle;
    }

    public void setLineWidth(int lineWidth) {
        mLineWidth = lineWidth;
    }

    public ProgressbarView(Context context) {
        this(context, null);
    }

    public ProgressbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        holder = this.getHolder();  //获取holder
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mLightPaint = new Paint();
        mLightPaint.setColor(mLightColorId);

        mDeepPaint = new Paint();
        mDeepPaint.setColor(mDeepColorId);

        mShadePaint = new Paint();
        mShadePaint.setColor(mBackgroundColor);

        mIsPlaying = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsPlaying = false;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsPlaying = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mIsPlaying = false;
        super.onDetachedFromWindow();
    }

    @Override
    public void run() {

        while (mIsPlaying) {
            //开始画的时间
            Canvas canvas = holder.lockCanvas();//获取画布
            canvas.drawColor(mBackgroundColor);

            final int height = getMeasuredHeight();
            final int width = getMeasuredWidth();
            final float offsetX = (float) (Math.tan(mAngle) * height);
            final float startWidth = 0;
            final float limitWidth = (float) (width * mProgress);
            final float lastLimit = limitWidth + offsetX;

            for(float tempWidth = startWidth;tempWidth < lastLimit ; tempWidth += mLineWidth * 2) {
                final float startX = tempWidth;

                final Paint firstPaint;
                final Paint secondPaint;
                final float distance;
                if(mDistance < mLineWidth) {
                    distance = mDistance;
                    firstPaint = mDeepPaint;
                    secondPaint = mLightPaint;
                }else {      //mDistance >= mLineWidth && mDistance <= 2 * mLineWidth
                    distance = mDistance - mLineWidth;
                    firstPaint = mLightPaint;
                    secondPaint = mDeepPaint;
                }

                float itemStartX = startX;
                float itemWidth;

                // first part
                if(itemStartX + distance > lastLimit) {
                    itemWidth = lastLimit - startX;
                } else {
                    itemWidth = distance;
                }
                final float deepFirstWidth = itemWidth;
                final float deepFirstLeftTopX = startX - offsetX;
                final float deepFirstRightTopX = deepFirstLeftTopX + deepFirstWidth;
                final float deepFirstLeftBottomX = startX;
                final float deepFirstRightBottomX = deepFirstLeftBottomX + deepFirstWidth;

                Path deepFirstPath = new Path();
                deepFirstPath.moveTo(deepFirstLeftTopX, 0);
                deepFirstPath.lineTo(deepFirstLeftBottomX, height);
                deepFirstPath.lineTo(deepFirstRightBottomX, height);
                deepFirstPath.lineTo(deepFirstRightTopX, 0);
                deepFirstPath.close();
                canvas.drawPath(deepFirstPath, firstPaint);
                itemStartX += itemWidth;
                if(itemStartX >= lastLimit)break;

                // second part
                if(itemStartX + mLineWidth > lastLimit) {
                    itemWidth = lastLimit - itemStartX;
                } else {
                    itemWidth = mLineWidth;
                }
                final float lightFirstWidth = itemWidth;
                final float lightFirstLeftTopX = deepFirstRightTopX;
                final float lightFirstRightTopX = lightFirstLeftTopX + lightFirstWidth;
                final float lightFirstLeftBottomX = deepFirstRightBottomX;
                final float lightFirstRightBottomX = lightFirstLeftBottomX + lightFirstWidth;

                Path lightFirstPath = new Path();
                lightFirstPath.moveTo(lightFirstLeftTopX, 0);
                lightFirstPath.lineTo(lightFirstLeftBottomX, height);
                lightFirstPath.lineTo(lightFirstRightBottomX, height);
                lightFirstPath.lineTo(lightFirstRightTopX, 0);
                lightFirstPath.close();
                canvas.drawPath(lightFirstPath, secondPaint);
                itemStartX += itemWidth;
                if(itemStartX >= lastLimit)break;

                // third part
                if(itemStartX + mLineWidth - distance > lastLimit) {
                    itemWidth = lastLimit - itemStartX;
                } else {
                    itemWidth = mLineWidth - distance;
                }
                final float deepSecondWidth = itemWidth;
                final float deepSecondLeftTopX = lightFirstRightTopX;
                final float deepSecondRightTopX = deepSecondLeftTopX + deepSecondWidth;
                final float deepSecondLeftBottomX = lightFirstRightBottomX;
                final float deepSecondRightBottomX = deepSecondLeftBottomX + deepSecondWidth;

                Path deepSecondPath = new Path();
                deepSecondPath.moveTo(deepSecondLeftTopX, 0);
                deepSecondPath.lineTo(deepSecondLeftBottomX, height);
                deepSecondPath.lineTo(deepSecondRightBottomX, height);
                deepSecondPath.lineTo(deepSecondRightTopX, 0);
                deepSecondPath.close();
                canvas.drawPath(deepSecondPath, firstPaint);
            }

            Path firstShadePath = new Path();
            firstShadePath.addRect(startWidth - offsetX, 0, startWidth, height, Path.Direction.CW);
            firstShadePath.transform(new Matrix());
            canvas.drawPath(firstShadePath, mShadePaint);

            Path lastShadePath = new Path();
            lastShadePath.addRect(limitWidth, 0, limitWidth + offsetX, height, Path.Direction.CW);
            canvas.drawPath(lastShadePath, mShadePaint);

            holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
            try {
                if(!mIsPlaying)continue;
                Thread.sleep(REFRESH_TIME);
                mDistance = (mDistance + 10) % (mLineWidth * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
