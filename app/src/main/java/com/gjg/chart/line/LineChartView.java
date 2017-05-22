package com.gjg.chart.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/18
 */
public class LineChartView extends View {
    // 整体的背景色
    private int bgColor = Color.parseColor("#FFFFFF");

    // 单数列的背景色
    private int singleColumnFillColor = Color.parseColor("#1137B2F3");

    // 双数列的背景色
    private int doubleColumnFillColor = Color.parseColor("#FFFFFF");

    // 表格的线颜色
    private int xyLineColor = Color.parseColor("#e4e4e4");

    // X、Y轴文字的颜色
    private int xyTextolor = Color.parseColor("#999999");

    // X轴选中的文字的颜色
    private int xSelectedTextcolor = Color.parseColor("#0c199c");

    // X轴选中的文字边框颜色
    private int xSelectedTextRectStrokecolor = Color.parseColor("#08148b");

    // X轴选中的文字填充
    private int xSelectedTextRectFillcolor = Color.parseColor("#505fee");

    //绘制x，y轴线的画笔/表格线的画笔
    private Paint xyPaint;

    //绘制单数列背景的画笔
    private Paint singleColumnPaint;

    //绘制双数列背景的画笔
    private Paint doubleColumnPaint;

    //绘制X、y轴文字的画笔
    private Paint xTextPaint;
    private Paint yTextPaint;

    //绘制X轴选中状态圆角框的画笔
    private Paint xTextRectPaint;
    //绘制X轴选中状态圆角框填充区域的画笔
    private Paint xTextRectAreaPaint;

    //截取层的画笔，用于覆盖折线超出的部分
    private Paint layerPaint;

    //去除Padding的width
    private int width;

    //去除Padding的height
    private int height;

    //表格线的宽度
    private int xyLineWidth = dpToPx(1);

    //XY轴文字的大小
    private int xyTextSize = spToPx(12);

    //X轴选中文字边框的Padding
    private int xSelectPadding = dpToPx(5);

    //X轴选中文字边框的圆角
    private int xSelectRadius = dpToPx(5);

    //x轴的原点坐标
    private int xOri;

    //y轴的原点坐标
    private int yOri;

    //y轴的原点坐标
    private int yTop;

    //第一个点X的坐标
    private float xInit;

    //第一个点对应的最大Y坐标
    private float maxXInit;

    //第一个点对应的最小X坐标
    private float minXInit;

    //x轴各个坐标点水平间距
    private int interval = dpToPx(50);

    //点击的点对应的X轴的第几个点，默认1
    private int selectIndex = 1;

    //X轴的文字距离X轴的偏移量
    private int xOffset = dpToPx(5);

    //Y轴的文字距离Y轴的偏移量
    private int yOffset = dpToPx(15);

    // 默认Y轴放5个值（越多显示的值越精细）
    private int numberOfY = 5;

    //X轴的文字的最大边界
    int[] maxTextX;

    //Y轴文字的最大边界
    int[] maxTextY;

    //X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
    private Rect xValueRect;

    //X轴的值集合
    private List<String> xValues = new ArrayList<>();
    // 多条线的数据集合
    private List<LineData> lineDatas;

    //转换后的数据
    private List<List<Float>> pointList;
    //转换后折线色值的集合
    private List<Integer> lineColorList;

    //纵轴最大值
    float maxNumber = 0.0f;


    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        initSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 整体的背景色
        canvas.drawColor(bgColor);
        // 计算出X轴平均后的表格线的坐标
        List<PointF> listX = initNumberOfX();
        // 计算出Y轴平均后的表格线的坐标
        List<PointF> listY = initNumberOfY();

        // 画折线图X的单位
        setXTitle(listX, canvas);
        // 画折线图Y的单位，同时计算出最大的Y轴值
        setYTitle(listY, canvas);
        //重新开一个图层
        int layerIdTable = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        // 根据需求，对每一个框做不同的填充颜色
        fillColor(listX, canvas);
        // 绘制纵向的（网格线）
        float stopY = yTop + xyLineWidth / 2;
        for (int i=1;i<listX.size();i++) {
            PointF point = listX.get(i);
            canvas.drawLine(point.x, yOri,
                    point.x, stopY, xyPaint);
        }
        // 绘制横向的（网格线）
        float stopX = 0;
        if(interval * (xValues.size()-1) < width - xOri){
            stopX = width - xyLineWidth / 2 - xValueRect.width()/2;
        }else{
            stopX = width - xyLineWidth / 2 - xValueRect.width()/2 + xEndOffset;
        }
        for (int i=1;i<listY.size();i++) {
            PointF point = listY.get(i);
            canvas.drawLine(xOri, point.y,stopX
                    , point.y, xyPaint);
        }
        // 将折线超出x轴坐标的部分截取掉
        layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RectF tableRectFLeft = new RectF(0, yTop, xOri - xyLineWidth / 2, yOri);
        canvas.drawRect(tableRectFLeft, layerPaint);
//        RectF tableRectFRight = new RectF(width - xValueRect.width() / 2 + xyLineWidth / 2, yTop, width, yOri);
//        canvas.drawRect(tableRectFRight, layerPaint);
        layerPaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layerIdTable);

        //重新开一个图层
        int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        // 计算像素位置
        List<List<PointF>> positionList = countListPosition(listX);
        // 画折线
        drawChart(canvas, positionList);
        // 将折线超出x轴坐标的部分截取掉
        layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RectF rectFLeft = new RectF(0, yTop, xOri, yOri);
        canvas.drawRect(rectFLeft, layerPaint);
//        RectF rectFRight = new RectF(width - xValueRect.width() / 2, yTop, width, yOri);
//        canvas.drawRect(rectFRight, layerPaint);
        layerPaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layerId);
        //横总坐标
        canvas.drawLine(xOri, yOri,
                stopX, yOri, xyPaint);
        canvas.drawLine(xOri, yOri,
                xOri, stopY, xyPaint);
    }

    private float startX;
    private float xEndOffset;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);//当该view获得点击事件，就请求父控件不拦截事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (interval * (xValues.size() - 1) > (width - xOri - xValueRect.width() / 2)) {//当期的宽度不足以呈现全部数据
                    float dis = event.getX() - startX;
                    startX = event.getX();
                    if (xInit + dis < minXInit) {
                        xInit = minXInit;
                    } else if (xInit + dis > maxXInit) {
                        xInit = maxXInit;
                    } else {
                        xInit = xInit + dis;
                    }
                    if(xInit - minXInit < xValueRect.width()/2){
                        xEndOffset = 0;
                    }else{
                        xEndOffset = xValueRect.width()/2;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                clickAction(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * 点击X轴坐标或者折线节点
     */
    private void clickAction(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        for (int i = 0; i < xValues.size(); i++) {
            String text = xValues.get(i);
            Rect rect = getTextBounds(text, xTextPaint);
            float x = xInit + interval * i - rect.width() / 2;
            float y = yOri + yOffset + rect.height();
            if (eventX >= x &&
                    eventX <= x + rect.width() &&
                    eventY >= y - rect.height() &&
                    eventY <= y &&
                    selectIndex != i + 1) {
                selectIndex = i + 1;
                invalidate();
                if (onTabSelectedListener != null) {
                    List<Float> yDatas = new ArrayList<>();
                    for (List<Float> d : pointList) {
                        yDatas.add(d.get(selectIndex - 1));
                    }
                    String xV = xValues.get(selectIndex - 1);
                    onTabSelectedListener.onTabSelected(selectIndex, xV, yDatas);
                }
                return;
            }
        }
    }

    /**
     * 折线
     */
    private void drawChart(Canvas canvas, List<List<PointF>> positionList) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);// 默认线宽为3，到时候提升到全局变量，用于设置
        for (int i = 0; i < positionList.size(); i++) {
            if (lineColorList != null && lineColorList.get(i) != null) {
                paint.setColor(lineColorList.get(i));
            }
            int startPostion = getStartPosition(pointList.get(i));
            for (int j = startPostion; j < positionList.get(i).size() - 1; j++) {
                //绘制折线
                canvas.drawLine(positionList.get(i).get(j).x, positionList.get(i).get(j).y,
                        positionList.get(i).get(j + 1).x, positionList.get(i).get(j + 1).y, paint);
            }
        }
    }

    private int getStartPosition(List<Float> positionListItem) {
        int startPostion = 0;
        for (int j = 0; j < positionListItem.size() - 1; j++) {
            if (positionListItem.get(j) > 0) {
                startPostion = j;
                break;
            }
        }
        return startPostion;
    }

    /**
     * 计算每个点的像素位置
     */
    private List<List<PointF>> countListPosition(List<PointF> listX) {
        List<List<PointF>> positionList = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            List<PointF> positionInList = new ArrayList<>();
            for (int j = 0; j < pointList.get(i).size(); j++) {
                PointF point = new PointF();
                Float z = pointList.get(i).get(j);
                point.x = listX.get(j).x;
                point.y = listX.get(j).y - (yOri - yTop) * z / maxNumber;
                positionInList.add(point);
            }
            positionList.add(positionInList);
        }
        return positionList;
    }


    private void initPaint() {
        xyPaint = new Paint();
        xyPaint.setAntiAlias(true);
        xyPaint.setStrokeWidth(xyLineWidth);
        xyPaint.setStrokeCap(Paint.Cap.ROUND);
        xyPaint.setColor(xyLineColor);

        singleColumnPaint = new Paint();
        singleColumnPaint.setStyle(Paint.Style.FILL);
        singleColumnPaint.setColor(singleColumnFillColor);

        doubleColumnPaint = new Paint();
        doubleColumnPaint.setStyle(Paint.Style.FILL);
        doubleColumnPaint.setColor(doubleColumnFillColor);

        xTextPaint = new Paint();
        xTextPaint.setAntiAlias(true);
        xTextPaint.setTextSize(xyTextSize);
        xTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xTextPaint.setColor(xyTextolor);
        xTextPaint.setTextAlign(Paint.Align.LEFT);
        xTextPaint.setStyle(Paint.Style.STROKE);

        yTextPaint = new Paint();
        yTextPaint.setAntiAlias(true);
        yTextPaint.setTextSize(xyTextSize);
        yTextPaint.setStrokeCap(Paint.Cap.ROUND);
        yTextPaint.setColor(xyTextolor);
        yTextPaint.setStyle(Paint.Style.STROKE);

        xTextRectPaint = new Paint();
        xTextRectPaint.setAntiAlias(true);
        xTextRectPaint.setStrokeWidth(xyLineWidth);
        xTextRectPaint.setColor(xSelectedTextRectStrokecolor);
        xTextRectPaint.setStyle(Paint.Style.STROKE);

        xTextRectAreaPaint = new Paint();
        xTextRectAreaPaint.setAntiAlias(true);
        xTextRectAreaPaint.setStrokeWidth(0);
        xTextRectAreaPaint.setColor(xSelectedTextRectFillcolor);
        xTextRectAreaPaint.setStyle(Paint.Style.FILL);

        layerPaint = new Paint();
        layerPaint.setStyle(Paint.Style.FILL);
        layerPaint.setColor(0xffffffff);
    }

    private void initSize() {
        maxTextX = calculateMaxTextX();
        maxTextY = calculateMaxTextY();
        yTop = 0 + maxTextY[1] / 2 + xyLineWidth / 2;
        xValueRect = new Rect(0, 0, maxTextX[0] + 2 * xSelectPadding, maxTextX[1] + 2 * xSelectPadding);
        xEndOffset = xValueRect.width()/2;
        xOri = maxTextY[0] + xOffset + xyLineWidth / 2;
        xInit = xOri;
        yOri = height - (maxTextX[1] + yOffset + 2 * xSelectPadding + xyLineWidth / 2);
        interval = calculateInterval();
        minXInit = width - xValueRect.width() / 2 - interval * (xValues.size() - 1);
        maxXInit = xInit;
    }

    private int calculateInterval() {
        int iter = 0;
        if (xValues.size() > 7) {
            iter = (width - xOri - xValueRect.width() / 2) / (7 - 1);
        } else {
            iter = (width - xOri - xValueRect.width() / 2) / (xValues.size() - 1);
        }
        return iter;
    }

    /**
     * x轴坐标显示
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setXTitle(List<PointF> listX, Canvas canvas) {
        for (int i = 0; i < xValues.size(); i++) {
            float x = listX.get(i).x;
            float y = listX.get(i).y;
            //绘制X轴文本
            String text = xValues.get(i);
            Rect rect = getTextBounds(text, xTextPaint);
            float textX = x - rect.width() / 2;
            float textY = y + yOffset + rect.height();
            if (i == selectIndex - 1) {
                if (x >= xOri - xValueRect.width() / 2) {//只绘制从文字最小显示起点区域
                    float left = x - xValueRect.width() / 2;
                    float top = y + yOffset - (xValueRect.height() / 2 - rect.height() / 2);
                    canvas.drawRoundRect(left,
                            top,
                            left + xValueRect.width(),
                            top + xValueRect.height(),
                            xSelectRadius, xSelectRadius, xTextRectAreaPaint);
                    canvas.drawRoundRect(left,
                            top,
                            left + xValueRect.width(),
                            top + xValueRect.height(),
                            xSelectRadius, xSelectRadius, xTextRectPaint);
                    //如果是选中状态，绘制边框
                    xTextPaint.setColor(xSelectedTextcolor);
                    canvas.drawText(text, 0, text.length(), textX, textY, xTextPaint);
                }
            } else {
                if (x >= xOri - rect.width() / 2) {//只绘制从文字最小显示起点区域
                    xTextPaint.setColor(xyTextolor);
                    canvas.drawText(text, 0, text.length(), textX, textY, xTextPaint);
                }
            }
        }
    }

    /**
     * y轴坐标显示
     */
    private void setYTitle(List<PointF> listY, Canvas canvas) {
        float stepNumber = maxNumber / numberOfY;

        for (int i = 0; i < numberOfY; i++) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String v = decimalFormat.format(stepNumber * i);//format 返回的是字符串
            String text = String.valueOf(v);
            Rect rect = getTextBounds(text, yTextPaint);
            canvas.drawText(text, 0, text.length(), xOri - xyLineWidth - rect.width() - xOffset, listY.get(i).y + rect.height() / 2, yTextPaint);
        }
    }

    /**
     * 绘制指定区域背景色
     */
    private void fillColor(List<PointF> listX, Canvas canvas) {
        for (int i = 0; i < xValues.size() - 1; i++) {
            if (i % 2 == 0) {
                canvas.drawRect(listX.get(i).x, yTop, listX.get(i + 1).x, yOri, singleColumnPaint);
            } else {
                canvas.drawRect(listX.get(i).x, yTop, listX.get(i + 1).x, yOri, doubleColumnPaint);
            }

        }
    }

    /**
     * 计算出X轴平均后的坐标
     */

    private List<PointF> initNumberOfX() {
        List<PointF> list = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            PointF point = new PointF();
            point.y = yOri;
            point.x = xInit + interval * i;
            list.add(point);
        }
        return list;
    }

    /**
     * 计算出Y轴平均后的坐标
     */
    private List<PointF> initNumberOfY() {
        int yInterval = (yOri - yTop) / (numberOfY - 1);
        List<PointF> list = new ArrayList<>();
        for (int i = 0; i < numberOfY; i++) {
            PointF point = new PointF();
            point.x = xOri;
            point.y = yOri - yInterval * i;
            list.add(point);
        }
        return list;
    }

    /**
     * 计算X轴中文字的最大高度
     *
     * @return
     */
    private int[] calculateMaxTextX() {
        int[] x = new int[2];
        int maxW = 0;
        int maxH = 0;
        for (String xValue : xValues) {
            Rect rect = getTextBounds(xValue, xTextPaint);
            int w = rect.width();
            if (w > maxW) {
                maxW = w;
            }
            int h = rect.height();
            if (h > maxH) {
                maxH = h;
            }
        }
        x[0] = maxW;
        x[1] = maxH;
        return x;
    }

    /**
     * 计算Y轴中文字的最大宽度
     *
     * @return
     */
    private int[] calculateMaxTextY() {
        int[] y = new int[2];
        for (int i = 0; i < pointList.size(); i++) {
            for (int j = 0; j < pointList.get(i).size(); j++) {
                if (pointList.get(i).get(j) > maxNumber) {
                    maxNumber = pointList.get(i).get(j);
                }
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String v = decimalFormat.format(maxNumber);//format 返回的是字符串
        String text = String.valueOf(v);
        Rect rect = getTextBounds(text, yTextPaint);
        y[0] = rect.width();
        y[1] = rect.height();
        return y;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 300;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 450;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 获取丈量文本的矩形
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * dp转化成为px
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * sp转化为px
     */
    private int spToPx(int sp) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (scaledDensity * sp + 0.5f * (sp >= 0 ? 1 : -1));
    }

    /**
     * 设置数据集
     * LineData中的数据集的大小应该与XValue的集合的大小相等
     * 且该方法必须在外部调用
     *
     * @param xDatas
     * @param datas
     */
    public void setDataResource(List<String> xDatas, List<LineData> datas) throws Exception {
        if (xDatas == null || datas == null || xDatas.size() == 0 || datas.size() == 0) {
            throw new Exception("data source illegal");
        }
        int xSize = xDatas.size();
        for (LineData lineData : datas) {
            if (null == lineData || null == lineData.getLineValues() || 0 == lineData.getLineValues().size()) {
                throw new Exception("data source illegal");
            }
            if (xSize != lineData.getLineValues().size()) {
                throw new Exception("data source illegal");
            }
        }
        this.xValues = xDatas;
        this.lineDatas = datas;
        lineColorList = new ArrayList<>();
        pointList = new ArrayList<>();
        for (LineData lineData : lineDatas) {
            lineColorList.add(lineData.getLineColor());
            List<Float> yDatas = lineData.getLineValues();
            pointList.add(yDatas);
        }
        initSize();
        invalidate();
    }

    public interface onTabSelectedListener {
        void onTabSelected(int position, String xValue, List<Float> yData);
    }

    private onTabSelectedListener onTabSelectedListener;

    public void setOnTabSelectedListener(LineChartView.onTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }
}
