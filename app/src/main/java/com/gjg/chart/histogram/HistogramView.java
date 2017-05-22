package com.gjg.chart.histogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HistogramView extends View {

    private Paint xLinePaint;// 坐标轴 轴线 画笔：
    private Paint titlePaint;// 绘制文本的画笔
    private Paint paint;// 矩形画笔 柱状图的样式信息
    private float[] rates;
    private int[] progress;
    private int[] aniProgress;// 实现动画的值
    private int[] colors;// 柱子的颜色
    private int[] text;// 设置点击事件，显示哪一条柱状的信息
    // 坐标轴底部的星期数
    private String[] xWeeks;
    private int flag;// 是否使用动画

    private HistogramAnimation ani;

    private Paint topDesPaint;//顶部描述的画笔
    private Paint desPaint;//顶部单位描述的画笔


    private int topHeight = dp2px(70);//标题区域的高度
    private int bottomHeight = dp2px(30);//底部预留高度，绘制标签和分割线等
    private int columnWidth = dp2px(50);//柱子的宽度
    private int columOffset = dp2px(15);//真正柱子区域的左右偏移量
    private int topDesLineWidth = dp2px(40);//顶部标题的左右两边的横线的宽度
    private int topDesLineOffset = dp2px(5);//顶部标题的左右两边的横线距离中间文字的偏移量
    private int maxValue;//最大数据值
    private int columnTopOffset = 0;

    public HistogramView(Context context) {
        super(context);
        init();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rates = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        progress = new int[rates.length];
        for (int i = 0; i < rates.length; i++) {
            progress[i] = (int) (25 * 10000 * rates[i]);
        }
        xWeeks = new String[]{"柱子一", "柱子二", "柱子三", "柱子四"};
        text = new int[]{1, 1, 1, 1};
        aniProgress = new int[]{0, 0, 0, 0};
        maxValue = (int) (50 * 10000 * getMax(rates));
        colors = new int[]{Color.parseColor("#1C86D5"), Color.parseColor("#F5AA28"), Color.parseColor("#83D829"), Color.parseColor("#D75356")};
        ani = new HistogramAnimation();
        ani.setDuration(2000);


        // 给画笔设置颜色
        paint = new Paint();
        paint.setColor(Color.BLUE);

        xLinePaint = new Paint();
        xLinePaint.setColor(Color.parseColor("#A8A8A8"));
        xLinePaint.setStrokeWidth(1.0f);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.parseColor("#858585"));

        desPaint = new Paint();
        desPaint.setAntiAlias(true);
        desPaint.setColor(Color.parseColor("#4A4A4A"));
        desPaint.setTextSize(sp2px(15));

        topDesPaint = new Paint();
        topDesPaint.setColor(Color.parseColor("#A8A8A8"));
        topDesPaint.setTextSize(sp2px(16));

    }

    public void start(int flag) {
        this.flag = flag;
        this.startAnimation(ani);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        int columBottom = height - bottomHeight;//柱子的起始底部
        int leftHeight = columBottom - topHeight;// 可用来画柱子的高度

        //绘制顶部的描述
        Rect topDesRect = new Rect();
        String topDes = "一表格描述区域";
        topDesPaint.getTextBounds(topDes, 0, topDes.length(), topDesRect);
        int topDesLength = topDesRect.width();
        int topDesHeight = topDesRect.height();
        float topDesStartX = width / 2.0f - topDesLength / 2.0f;
        float topDesStartY = topHeight / 2.0f - topDesHeight / 2.0f;
        topDesPaint.setTextAlign(Align.LEFT);
        canvas.drawText(topDes, topDesStartX, topDesStartY, topDesPaint);
        float topDesLineStartY = topDesStartY - topDesHeight / 2.0f;
        canvas.drawLine(topDesStartX - topDesLineWidth - topDesLineOffset,
                topDesLineStartY,
                topDesStartX - topDesLineOffset,
                topDesLineStartY, topDesPaint);
        canvas.drawLine(topDesStartX + topDesLength + topDesLineOffset,
                topDesLineStartY,
                topDesStartX + topDesLength + topDesLineOffset + topDesLineWidth,
                topDesLineStartY, topDesPaint);

        // 绘制 Y 周坐标
        titlePaint.setTextAlign(Align.LEFT);
        titlePaint.setTextSize(sp2px(16));
        titlePaint.setAntiAlias(true);
        titlePaint.setStyle(Paint.Style.FILL);

        int xAxisLength = width - columOffset * 2;//x轴的总长度
        int columCount = xWeeks.length;
        float step = xAxisLength / columCount;

        float start = columOffset + step / 2.0f;//起始中点
        // 绘制矩形
        if (aniProgress != null && aniProgress.length > 0) {
            for (int i = 0; i < aniProgress.length; i++) {// 循环遍历将柱状图形画出来
                //绘制柱子
                int value = aniProgress[i];
                paint.setAntiAlias(true);// 抗锯齿效果
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(sp2px(15));// 字体大小
                paint.setColor(colors[i]);// 字体颜色
                RectF rect = new RectF();// 柱状图的形状
                rect.left = start - columnWidth / 2.0f + step * i;
                rect.right = rect.left + columnWidth;
                float rh = leftHeight * 1.0f * value / maxValue;//柱子的相对高度
                rect.top = columBottom - rh - columnTopOffset;
                rect.bottom = columBottom;
                canvas.drawRect(rect, paint);

                //绘制底部文字
                Rect tRect = new Rect();
                titlePaint.getTextBounds(xWeeks[i], 0, xWeeks[i].length(), tRect);
                int textLength = tRect.width();
                int textHeight = tRect.height();
                float startX = start - textLength / 2.0f + step * i;
                float startY = columBottom + textHeight + dp2px(10);
                // text, baseX, baseY, textPaint
                canvas.drawText(xWeeks[i], +startX, startY, titlePaint);

                // 是否显示柱状图上方的数字，绘制顶部文字
                int TRUE = 1;
                if (this.text[i] == TRUE) {
                    Rect vRect = new Rect();
                    String strValue = String.valueOf(value);
                    paint.getTextBounds(strValue, 0, strValue.length(), vRect);
                    int vtextLength = vRect.width();
                    int vtextHeight = vRect.height();
                    float vstartX = start - vtextLength / 2.0f + step * i;
                    float vstartY = rect.top - vtextHeight;
                    canvas.drawText(strValue, vstartX, vstartY, paint);
                }
            }
        }
        // 绘制底部的线条
        float bottomLineStartX = start - columnWidth / 2.0f;
        float strokeWidth = xLinePaint.getStrokeWidth();
        float bottomLineEndY = bottomLineStartX + step * 3 + columnWidth - strokeWidth;
        canvas.drawLine(bottomLineStartX, columBottom, bottomLineEndY, columBottom, xLinePaint);
        //绘制顶部单位描述
        Rect desRect = new Rect();
        String des = "单位/元";
        desPaint.getTextBounds(des, 0, des.length(), desRect);
        int desLength = desRect.width();
        int desHeight = desRect.height();
        float desStartX = bottomLineEndY - desLength;
        float desStartY = columBottom - leftHeight - desHeight - columnTopOffset;
        canvas.drawText(des, desStartX, desStartY, desPaint);
    }

    /**
     * 集成animation的一个动画类
     */
    private class HistogramAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f && flag == 2) {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = (int) (progress[i] * interpolatedTime);
                }
            } else {
                System.arraycopy(progress, 0, aniProgress, 0, aniProgress.length);
//                for (int i = 0; i < aniProgress.length; i++) {
//                    aniProgress[i] = progress[i];
//                }
            }
            invalidate();
        }
    }

    /**
     * 拖动滑块后的数据变化
     */
    public void changeProgress(int progress) {
        int current = 1000 + progress;

        for (int i = 0; i < rates.length; i++) {
            aniProgress[i] = (int) (current * rates[i]);
        }
        postInvalidate();
    }

    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

    private int sp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (v * value + 0.5f);
    }

    public void initData(float r[]) {
        this.rates = r;
        progress = new int[rates.length];
        for (int i = 0; i < rates.length; i++) {
            progress[i] = (int) (10 * 10000 * rates[i]);
        }
        columnTopOffset = dp2px(5);
        maxValue = (int) (50 * 10000 * getMax(rates));
    }
    /**
     * 取出数组中的最大值
     * @param arr
     * @return
     */
    public static float getMax(float[] arr) {
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
}