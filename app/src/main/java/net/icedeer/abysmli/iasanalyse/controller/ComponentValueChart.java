package net.icedeer.abysmli.iasanalyse.controller;

import android.graphics.Color;
import android.graphics.Paint;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;

/**
 * Created by Li, Yuan on 12.09.15.
 * All Right reserved!
 */
public class ComponentValueChart {

    private int minValue = -10;
    private int maxValue = 100;
    private int step = 10;
    private int componentID;

    private final LineChartView value_chart;

    private final float[] _data = new float[AppSetting.valueGraphicWidth];

    public ComponentValueChart(LineChartView chart, int componentID) {
        value_chart = chart;
        if (componentID == 1) {
            minValue = -5;
            maxValue = 15;
            step = 4;
        } else if (componentID == 2) {
            minValue = 0;
            maxValue = 20;
            step = 4;
        } else if (componentID == 3) {
            minValue = 0;
            maxValue = 20;
            step = 4;
        } else if (componentID == 4) {
            minValue = 20;
            maxValue = 35;
            step = 3;
        }
    }

    public void generateChart() {
        LineSet dataSet = new LineSet();
        initDataSet(dataSet);
        dataSet.setColor(Color.parseColor("#FE2E2E"));
        dataSet.setThickness(1);
        value_chart.addData(dataSet);

        value_chart.setXLabels(AxisController.LabelPosition.NONE);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#F2F2F2"));
        value_chart.setGrid(ChartView.GridType.FULL, paint);

        Paint paint_threshold_up = new Paint();
        paint_threshold_up.setColor(Color.parseColor("#2E2EFE"));
        paint_threshold_up.setStrokeWidth(2);
        value_chart.setAxisBorderValues(minValue, maxValue, step);
        value_chart.show();
    }

    public void update(float newPoint) {
        System.arraycopy(_data, 1, _data, 0, AppSetting.valueGraphicWidth - 1);
        _data[49] = newPoint;
        value_chart.updateValues(0, _data);
        value_chart.notifyDataUpdate();
    }

    private void initDataSet(LineSet dataSet) {
        for (int i = 0; i < AppSetting.valueGraphicWidth; i++) {
            dataSet.addPoint(new Point("", 0));
            _data[i] = 0;
        }
    }
}
