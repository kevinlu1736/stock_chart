package com.ly.stockchartlib.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ly on 04/06/2017.
 */

public class TriStrokeBtn extends View {

    private int viewWidth;
    private int viewHeight;
    private Paint paint;
    private int strokeColor = Color.RED;
    private float strokeWidth = dp2px(2);

    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 2;

    private int direction = DIRECTION_LEFT;

    public TriStrokeBtn(Context context) {
        this(context, null);
    }

    public TriStrokeBtn(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriStrokeBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    private void initPaint(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(direction==DIRECTION_RIGHT){
            canvas.rotate(180, viewWidth/2f, viewHeight/2f);
        }
        float rightBoundary = viewWidth - strokeWidth;
        float leftBoundary = strokeWidth;
        float topBoundary = strokeWidth;
        float bottomBoundary = viewHeight-strokeWidth;
        float centerHeight = viewHeight / 2f;
        canvas.drawLine(leftBoundary, centerHeight, rightBoundary, topBoundary, paint);
        canvas.drawLine(rightBoundary, topBoundary, rightBoundary, bottomBoundary, paint);
        canvas.drawLine(rightBoundary, bottomBoundary, leftBoundary, centerHeight, paint);
        canvas.restore();

    }

    public interface OnPressListener{
        void onPressEnd(int id);
        void onPressStart(int id);
    }

    private OnPressListener onPressListener;

    public void setOnPressListener(OnPressListener onPressListener) {
        this.onPressListener = onPressListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            if(onPressListener!=null){
                onPressListener.onPressStart(getId());
            }
        }else if(action==MotionEvent.ACTION_UP){
            if(onPressListener!=null){
                onPressListener.onPressEnd(getId());
            }
        }else if(action==MotionEvent.ACTION_CANCEL){
            if(onPressListener!=null){
                onPressListener.onPressEnd(getId());
            }
        }
        return super.onTouchEvent(event);
    }

    /*helpers*/

    private float dp2px(float dp){
        return getResources().getDisplayMetrics().density*dp;
    }


    /*getters and setters*/

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
        invalidate();
    }
}
