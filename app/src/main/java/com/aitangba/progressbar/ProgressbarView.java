package com.aitangba.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by fhf11991 on 2016/9/20.
 */
public class ProgressbarView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "ProgressbarView";

    private static final int REFRESH_TIME = 15; //ms

    private SurfaceHolder holder;

    private Paint mLightPaint;
    private Paint mDeepPaint;
    private Paint mShadePaint;

    private int mDeepColorId = Color.parseColor("#91E13D");
    private int mLightColorId = Color.parseColor("#B9EC83");
    private int mBackgroundColor = Color.WHITE;

    private boolean mIsPlaying;
    private int mDistance; //px 0 <--> 2 * mLineWidth
    private int mLineWidth = 80; //px
    private double mAngle = 45; //

    private double mStartProgress = 0;
    private double mProgress = (double) 3  / 4;

    private int mStepDistance = 4;
    private int mRefreshTime = REFRESH_TIME;

    /**
     * between 0 to 100
     * @param startProgress
     */
    public void setStartProgress(int startProgress) {
        mStartProgress = (double) startProgress / 100;
    }

    /**
     * between 0 to 100
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = (double) progress / 100;
        if(mProgress < mStartProgress) {
            mProgress = mStartProgress;
        }
    }

    /**
     * between 0 to 90
     * @param angle
     */
    public void setAngle(double angle) {
        mAngle = angle;
    }

    /**
     * between 0 to 2 * mLineWidth
     * @param stepDistance
     */
    public void setStepDistance(int stepDistance) {
        mStepDistance = stepDistance;
    }

    public void setRefreshTime(int refreshTime) {
        mRefreshTime = refreshTime;
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
        holder = this.getHolder();
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
            Canvas canvas = holder.lockCanvas();
            if(canvas == null) return;

            //draw background color
            canvas.drawColor(mBackgroundColor);

            final int width = getMeasuredWidth();
            final int height = getMeasuredHeight();
            final float startWidth = (int) (width * mStartProgress);
            final float limitWidth = (int) (width * mProgress);
            final RectF clipAreaRectF = new RectF(startWidth, 0, limitWidth, height);

            //draw first front color
            canvas.drawRect(clipAreaRectF, mLightPaint);

            final int lineWidth = mLineWidth; //为保障线程安全，所有变量用final
            final int offsetX = - 2 * lineWidth + mDistance; //由于发生裁剪，所以无需担心多出的部分
            final float radianTanValue = (float) Math.tan(Math.toRadians(mAngle));
            final float offsetDistance = height * radianTanValue;
            final float totalWidth = limitWidth - startWidth + Math.abs(offsetDistance) + 2 * lineWidth;
            final int drawCount = (int) Math.ceil(totalWidth / lineWidth);
            final float drawStartX = (offsetDistance > 0 ? startWidth - offsetDistance : startWidth) + offsetX;

            final Path frontArea = new Path();
            frontArea.moveTo(drawStartX, 0);
            for(int i = 0 ; i < drawCount ; i ++) {
                float horizontalStartX = drawStartX + lineWidth * i;
                float horizontalEndX = horizontalStartX + lineWidth;

                final int lineHeight = i % 2 == 0 ? height : 0;

                //draw vertical
                frontArea.lineTo(horizontalStartX, lineHeight);
                //draw horizontal
                frontArea.lineTo(horizontalEndX, lineHeight);
            }
            //last line
            if(drawCount % 2 != 0) {
                float horizontalX = drawStartX + lineWidth * drawCount;
                frontArea.lineTo(horizontalX, 0);
            }
            frontArea.close();

            //draw second front color
            canvas.clipRect(clipAreaRectF);
            canvas.skew(radianTanValue, 0);
            canvas.drawPath(frontArea, mDeepPaint);

            holder.unlockCanvasAndPost(canvas);

            try {
                if(!mIsPlaying)continue;
                Thread.sleep(mRefreshTime);
                mDistance = (mDistance + mStepDistance) % (2 * mLineWidth);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
