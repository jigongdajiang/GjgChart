package com.gjg.chart.pie;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/19
 */
public class PieChartView extends View implements ValueAnimator.AnimatorUpdateListener {
    private String TAG = getClass().getSimpleName();
    //旋转中Action
    private int ACTION_ROTATION = 1;
    //放大半径action
    private int ACTION_ENLARGE = 2;
    //当前Action
    private int ACTION_ = ACTION_ROTATION;
    //动画时长 毫秒
    private long DURATION = 1000;
    //动画时长 毫秒
    private long DURATION_GAIN = 2000;
    //默认圆弧颜色
    private String defaultColor = "#A9A9A9";
    private List<Double> mNumbers;
    private List<Integer> mColors;
    private List<Point> points;
    //画布宽高
    private int width, height;
    private double total;
    //默认的半径系数
    private double normalGain = 0.8;
    private RectF normalOval;
    private RectF selectOval;
    //画笔旋转角度(0~360)
    private float degree;
    //选中后的缩放系数(0~1.0f)
    private float gain;
    //中空圆的半径
    private float radiusCenter;
    //放大编译系数
    private int bigerGain = 30;
    //属性动画
    private ValueAnimator valueAnimator;
    //选中后的监听
    private OnItemChangedListener mOnItemChangedListener;
    //圆弧画笔
    private Paint paintArc;
    //中空圆画笔
    private Paint paintCenterCircle;
    private int select = -1;


    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        mNumbers = new ArrayList<>();
        mColors= new ArrayList<>();
        normalOval = new RectF();
        selectOval = new RectF();

        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setStyle(Paint.Style.FILL);
        paintArc.setAntiAlias(true);
        paintArc.setColor(Color.RED);

        paintCenterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCenterCircle.setStyle(Paint.Style.FILL);
        paintCenterCircle.setAntiAlias(true);
        paintCenterCircle.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int m = Math.min(width,height);
        setMeasuredDimension(m, m);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = height = Math.min(w,h);
        normalOval.left = (float) (-width/2 * normalGain);
        normalOval.right = (float) (width/2 * normalGain);
        normalOval.top = (float) (-height/2 * normalGain);
        normalOval.bottom = (float) (height/2 * normalGain);
        radiusCenter = width/8;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width/2,height/2);
        if (this.mNumbers == null || mNumbers.size() == 0 || this.total == 0f) {
            paintArc.setColor(Color.parseColor(defaultColor));
            canvas.drawArc(normalOval, 0F, 360F, true, paintArc);
            return;
        }
        canvas.rotate(degree);
        if (!mNumbers.isEmpty()) {
            int startAngle = 0;
            int sweepAngle = 0;
            for (int i = 0; i < mNumbers.size(); i++) {
                canvas.save();
                if (i == mNumbers.size() - 1) {
                    sweepAngle = 360 - startAngle;
                } else {
                    sweepAngle = (int) (mNumbers.get(i) * 1.0f / total * 360);
                }
                if (select >= 0 && i == select) {
                    selectOval.left = (float) (-width/2 * normalGain);
                    selectOval.right = (float) (width/2 * normalGain);
                    selectOval.top = (float) (-height/2 * normalGain);
                    selectOval.bottom = (float) (height/2 * normalGain);
                    Point point = points.get(select);
                    int middle = (point.x + point.y) / 2;
                    if (middle <= 90) {
                        int left = (int) (Math.cos(Math.toRadians(middle)) * bigerGain);
                        int top = (int) (Math.sin(Math.toRadians(middle)) * bigerGain);
                        setSelectOval(left,top,1,1);
                    }
                    if (middle > 90 && middle <= 180) {
                        middle = 180 - middle;
                        int top = (int) (Math.sin(Math.toRadians(middle)) * bigerGain);
                        int left = (int) (Math.cos(Math.toRadians(middle)) * bigerGain);
                        setSelectOval(left,top,-1,1);
                    }
                    if (middle > 180 && middle <= 270) {
                        middle = 270 - middle;
                        int left = (int) (Math.sin(Math.toRadians(middle)) * bigerGain);
                        int top = (int) (Math.cos(Math.toRadians(middle)) * bigerGain);
                        setSelectOval(left,top,-1,-1);
                    }
                    if (middle > 270 && middle <= 360) {
                        middle = 360 - middle;
                        int top = (int) (Math.sin(Math.toRadians(middle)) * bigerGain);
                        int left = (int) (Math.cos(Math.toRadians(middle)) * bigerGain);
                        setSelectOval(left,top,1,-1);
                    }
                    paintArc.setColor(getResources().getColor(mColors.get(i)));
                    canvas.drawArc(selectOval, startAngle, sweepAngle, true,
                            paintArc);
                } else {
                    paintArc.setColor(getResources().getColor(mColors.get(i)));
                    canvas.drawArc(normalOval, startAngle, sweepAngle, true,
                            paintArc);
                }
                canvas.restore();
                points.get(i).x = startAngle;
                points.get(i).y = startAngle + sweepAngle;
                startAngle += sweepAngle;
            }
        }
        canvas.drawCircle(0,0,radiusCenter,paintCenterCircle);
    }
    private void setSelectOval(float leftOffset,float topOffset,int leftP,int topP){
        selectOval.left += leftOffset * gain*leftP;
        selectOval.right += leftOffset * gain*leftP;
        selectOval.top += topOffset * gain*topP;
        selectOval.bottom += topOffset * gain*topP;
    }
    public void setDatas(List<Double> numbers,List<Integer> colors) {
        if(numbers == null || colors == null  || numbers.size() != colors.size()){
            return;
        }
        mNumbers.clear();
        mNumbers.addAll(numbers);
        mColors.clear();
        mColors.addAll(colors);
        points = new ArrayList<>();
        total = 0;
        for (Double item : numbers) {
            total += item;
            Point point = new Point();
            points.add(point);
        }
        rotation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int radius = 0;
            // 第一象限
            if (x >= width / 2 && y >= height / 2) {
                radius = (int) (Math.atan((y - height / 2) * 1.0f
                        / (x - width / 2)) * 180 / Math.PI);
            }
            // 第二象限
            if (x <= width / 2 && y >= height / 2) {
                radius = (int) (Math.atan((width / 2 - x)
                        / (y - height / 2))
                        * 180 / Math.PI + 90);
            }
            // 第三象限
            if (x <= width / 2 && y <= height / 2) {
                radius = (int) (Math.atan((height / 2 - y)
                        / (width / 2 - x))
                        * 180 / Math.PI + 180);
            }
            // 第四象限
            if (x >= width / 2 && y <= height / 2) {
                radius = (int) (Math.atan((x - width / 2)
                        / (height / 2 - y))
                        * 180 / Math.PI + 270);
            }
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                if (point.x <= radius && point.y >= radius) {
                    select = i;
                    if (mOnItemChangedListener != null)
                        this.mOnItemChangedListener.onItemChanged(i, mNumbers.get(select));
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gain();
                    return true;
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (this.ACTION_ == this.ACTION_ROTATION) {
            degree = Float.valueOf(valueAnimator.getAnimatedValue().toString());
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            gain = Float.valueOf(valueAnimator.getAnimatedValue().toString());
        }
        Log.e(TAG,"degree="+degree+"---gain="+gain);
        invalidate();
    }

    private void animateToValue() {
        if (this.ACTION_ == this.ACTION_ROTATION) {
            if (valueAnimator == null) {
                valueAnimator = createAnimator();
            }
            valueAnimator.setDuration(DURATION);
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            valueAnimator = createAnimator();
            valueAnimator.setDuration(DURATION_GAIN);
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.start();
    }

    private ValueAnimator createAnimator() {
        ValueAnimator valueAnimator = null;
        if (this.ACTION_ == this.ACTION_ROTATION) {
            valueAnimator = ValueAnimator.ofFloat(0, 360);
            valueAnimator.setDuration(DURATION);
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(DURATION_GAIN);
        }
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(this);
        return valueAnimator;
    }


    private void rotation() {
        this.ACTION_ = this.ACTION_ROTATION;
        animateToValue();
    }

    private void gain() {
        this.ACTION_ = this.ACTION_ENLARGE;
        animateToValue();
    }
    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.mOnItemChangedListener = listener;
    }

    public interface OnItemChangedListener {
        void onItemChanged(int index, double value);
    }
}