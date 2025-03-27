package com.ehaohai.robot.ui.cell;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ehaohai.robot.R;

/**
 * @author qc
 * 自定义电池控件
 */
public class BatteryView extends View {
    private int mPower = 100;
    private int orientation;
    private int width;
    private int height;
    private int mColor;

    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Battery);
        mColor = typedArray.getColor(R.styleable.Battery_batteryColor, 0xFFFFFFFF);
        orientation = typedArray.getInt(R.styleable.Battery_batteryOrientation, 0);
        mPower = typedArray.getInt(R.styleable.Battery_batteryPower, 100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        /**
         * recycle() :官方的解释是：回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
         * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，这就说明这个对象从现在可以被重用了。
         *TypedArray 内部持有部分数组，它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存。
         */
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //对View上的內容进行测量后得到的View內容占据的宽度
        width = getMeasuredWidth();
        //对View上的內容进行测量后得到的View內容占据的高度
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断电池方向    horizontal: 0   vertical: 1
        if (orientation == 0) {
            drawHorizontalBattery(canvas);
        } else {
            drawVerticalBattery(canvas);
        }
    }

    /**
     * 绘制水平电池
     *
     * @param canvas
     */
    private void drawHorizontalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth*2 - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r1, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        float offset = (width - strokeWidth * 3) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offset, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 30) {
            paint.setColor(getResources().getColor(R.color.text_color_red));
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(getResources().getColor(R.color.text_color_orange));
        }
        if (mPower >= 50) {
            paint.setColor(getResources().getColor(R.color.text_color_green));
        }
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth*2, height * 0.25f, width - strokeWidth, height * 0.75f);
        //设置电池头颜色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r3, paint);

        RectF r4 = new RectF(width - strokeWidth - strokeWidth_2, height * 0.40f, width-strokeWidth_2, height * 0.60f);
        canvas.drawRect(r4, paint);

        //绘制电量文本
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);  // 设置文本颜色
        textPaint.setTextSize(32);  // 设置字体大小
        textPaint.setAntiAlias(true); // 抗锯齿
        textPaint.setTextAlign(Paint.Align.CENTER); // 文字居中
        // 计算文字的垂直居中基线
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseline = height*0.5f - (fontMetrics.ascent + fontMetrics.descent) *0.5f;
        // 绘制文本到居中位置
        canvas.drawText(mPower+"%", width / 2f, baseline, textPaint);
    }

    /**
     * 绘制垂直电池
     *
     * @param canvas
     */
    private void drawVerticalBattery0(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = height / 20.0f;
        float strokeWidth2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        int headHeight = (int) (strokeWidth + 0.5f);
        RectF rect = new RectF(strokeWidth2, headHeight + strokeWidth2, width - strokeWidth2, height - strokeWidth2);
        canvas.drawRect(rect, paint);
        paint.setStrokeWidth(0);
        float topOffset = (height - headHeight - strokeWidth) * (100 - mPower) / 100.0f;
        RectF rect2 = new RectF(strokeWidth, headHeight + strokeWidth + topOffset, width - strokeWidth, height - strokeWidth);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect2, paint);
        RectF headRect = new RectF(width / 4.0f, 0, width * 0.75f, headHeight);
        canvas.drawRect(headRect, paint);
    }

    private void drawVerticalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);

        // 外边框（竖向）
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2 + strokeWidth * 2, width - strokeWidth_2, height - strokeWidth_2);
        paint.setColor(Color.WHITE);
        canvas.drawRect(r1, paint);

        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);

        // 计算电池电量填充区域（从底部向上填充）
        float offset = (height - strokeWidth * 3) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, height - offset, width - strokeWidth, height - strokeWidth);

        // 根据电池电量决定颜色
        if (mPower < 30) {
            paint.setColor(getResources().getColor(R.color.text_color_red));
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(getResources().getColor(R.color.text_color_orange));
        }
        if (mPower >= 50) {
            paint.setColor(getResources().getColor(R.color.text_color_green));
        }
        canvas.drawRect(r2, paint);

        // 画电池头（顶部）
        RectF r3 = new RectF(width * 0.25f, strokeWidth_2, width * 0.75f, strokeWidth * 2);
        paint.setColor(Color.WHITE);
        canvas.drawRect(r3, paint);

        // 电池头细节（顶部更小的部分）
        RectF r4 = new RectF(width * 0.40f, strokeWidth_2, width * 0.60f, strokeWidth + strokeWidth_2);
        canvas.drawRect(r4, paint);

/*        // 绘制电量文本
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 计算文字的垂直居中基线
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseline = height / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;
        canvas.drawText(mPower + "%", width / 2f, baseline, textPaint);*/
    }

    /**
     * 设置电池电量
     *
     * @param power
     */
    public void setPower(int power) {
        this.mPower = power;
        if (mPower < 0) {
            mPower = 100;
        }
        invalidate();//刷新VIEW
    }

    /**
     * 设置电池颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getPower() {
        return mPower;
    }
}