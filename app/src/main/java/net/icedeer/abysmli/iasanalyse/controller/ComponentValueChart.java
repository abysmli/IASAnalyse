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

    private final LineChartView value_chart;

    private final float[] _data = new float[AppSetting.valueGraphicWidth];

    public ComponentValueChart(LineChartView chart) {
        value_chart = chart;
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
        value_chart.setThresholdLine(90, paint_threshold_up);

        value_chart.setAxisBorderValues(-10, 110, 10);
        value_chart.show();
    }

    public void update(float newPoint) {
        System.arraycopy(_data, 1, _data, 0, 49);
        _data[49] = newPoint;
        value_chart.updateValues(0, _data);
        value_chart.notifyDataUpdate();
    }

    private void initDataSet(LineSet dataSet) {
        for (int i = 0; i < AppSetting.valueGraphicWidth; i++) {
            dataSet.addPoint(new Point("", 0));
            _data[i]=0;
        }
    }
}
