package com.aitangba.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by fhf11991 on 2016/9/20.
 */
public class ProgressbarView extends View {

    private static final int STATUS_INIT = 0;
    private static final int STATUS_FINISHED = 1;

    private Paint mLightPaint;
    private Paint mDeepPaint;
    private Paint mShadePaint;

    private int mDeepColorId = Color.parseColor("#11c876");
    private int mLightColorId = Color.parseColor("#41d391");
    private int mBackgroundColor = Color.parseColor("#b6c9de");

    private float mDistance; //px 0 <--> 2 * mLineWidth
    private int mLineWidth = 200; //px
    private double mAngle = 20; //

    private double mStartProgress = 0;
    private double mProgress = (double) 3  / 4;

    private Direction mDirection;

    private boolean mUseFinishColor = true; //
    private int mFinishColor;
    private CustomHandler mCustomHandler;

    public enum Direction {
        Left, Right
    }

    public ProgressbarView(Context context) {
        this(context, null);
    }

    public ProgressbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mLightPaint = new Paint();
        mLightPaint.setColor(mLightColorId);

        mDeepPaint = new Paint();
        mDeepPaint.setColor(mDeepColorId);

        mShadePaint = new Paint();
        mShadePaint.setColor(mBackgroundColor);

        mCustomHandler = new CustomHandler(this);

        //set default values
        setDirection(Direction.Right);
        setFinishColor(Color.parseColor("#ffe8a1"));
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public void setFinishColor(@ColorInt int finishColor) {
        mFinishColor = finishColor;
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

        if(mProgress == STATUS_INIT) {
            invalidate();
        } else if(mProgress == STATUS_FINISHED) {
            if(mUseFinishColor) {
                stopProgress();
            } else {
                if(!mCustomHandler.isRunning) {
                    mCustomHandler.start();
                }
            }
        } else {
            if(!mCustomHandler.isRunning) {
                mCustomHandler.start();
            }
        }
    }

    public void setUseFinishColor(boolean useFinishColor) {
        mUseFinishColor = useFinishColor;
    }

    public void stopProgress() {
        if(mCustomHandler.isRunning) {
            mCustomHandler.stop();
        }
        invalidate();
    }

    public double getProgress() {
        return mProgress * 100;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mProgress == STATUS_INIT){
            canvas.drawColor(mBackgroundColor);
            return;
        }

        if(mProgress == STATUS_FINISHED && mUseFinishColor) {
            canvas.drawColor(mFinishColor);
            return;
        }

        //draw background color
        canvas.drawColor(mBackgroundColor);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final float startPointX = (int) (width * mStartProgress);
        final float endPointX = (int) (width * mProgress);
        final RectF clipAreaRectF = new RectF(startPointX, 0, endPointX, height);

        //draw first front color
        canvas.drawRect(clipAreaRectF, mLightPaint);

        final int lineWidth = mLineWidth; //为保障线程安全，所有变量用final
        final int direction = mDirection == Direction.Right ? 1 : -1;
        final float radian = (float) Math.toRadians(mAngle);
        final float radianTanValue = (float) Math.tan(radian);
        final float offsetX = mDistance > lineWidth ?  -2 * lineWidth + mDistance  : mDistance; //offsetX
        final float paddingDistance = height * radianTanValue; //paddingDistance
        final float rectWidth = endPointX - startPointX; //rectWidth
        final float totalWidth = -offsetX + Math.abs(paddingDistance) + rectWidth;
        final int cycleCount = (int) Math.ceil((double) totalWidth / lineWidth);

        final float drawStartX = (paddingDistance > 0 ? startPointX - paddingDistance : startPointX)
                + (direction * offsetX);
        final Path rectPath = new Path();
        rectPath.addRect(drawStartX, 0, drawStartX + lineWidth, height, Path.Direction.CW);

        Path deepPath = new Path();
        deepPath.moveTo(drawStartX, 0);
        for(int i = 0 ; i < cycleCount; i ++) {
            deepPath.addPath(rectPath, i * 2 * lineWidth, 0);
        }

        //draw second front color
        canvas.clipRect(clipAreaRectF);
        canvas.skew(radianTanValue, 0);
        canvas.drawPath(deepPath, mDeepPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mUseFinishColor && mCustomHandler != null) {
            mCustomHandler.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mCustomHandler != null && mCustomHandler.isRunning) {
            mCustomHandler.stop();
        }
        super.onDetachedFromWindow();
    }

    private static class CustomHandler extends Handler {
        private final static int MSG_NEXT = 1;
        private final static int MSG_STOP = 2;
        private static final int DURATION_TIME = 70; // 36ms刷新一次

        private float mIndex = 0f; // 当前步伐，一个周期会刷新7次
        private WeakReference<ProgressbarView> mWeakReference;
        private boolean isRunning;

        public CustomHandler(ProgressbarView progressbarView) {
            mWeakReference = new WeakReference<>(progressbarView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mWeakReference == null|| mWeakReference.get() == null) {
                return;
            }
            ProgressbarView progressbarView = mWeakReference.get();

            if(msg.what == MSG_NEXT) {
                mIndex = (mIndex + 1) % 7;
                progressbarView.mDistance = mIndex / 7 * (2 * progressbarView.mLineWidth);
                progressbarView.invalidate();

                sendEmptyMessageDelayed(MSG_NEXT, DURATION_TIME);
            } else if(msg.what == MSG_NEXT) {
                removeMessages(MSG_NEXT);
                removeMessages(MSG_STOP);
            }
        }

        private void start() {
            isRunning = true;
            sendEmptyMessageDelayed(MSG_NEXT, DURATION_TIME);
        }

        private void stop() {
            isRunning = false;
            sendEmptyMessage(MSG_STOP);
        }
    }
}
