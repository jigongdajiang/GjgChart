package com.gjg.chart.line;

import java.util.List;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/18
 */
public class LineData {
    private List<Float> lineValues;
    private int lineColor;

    public LineData() {
    }

    public LineData(List<Float> lineValues, int lineColor) {
        this.lineValues = lineValues;
        this.lineColor = lineColor;
    }

    public List<Float> getLineValues() {
        return lineValues;
    }

    public void setLineValues(List<Float> lineValues) {
        this.lineValues = lineValues;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    @Override
    public String toString() {
        return "LineData{" +
                "lineValues=" + lineValues +
                ", lineColor=" + lineColor +
                '}';
    }
}
