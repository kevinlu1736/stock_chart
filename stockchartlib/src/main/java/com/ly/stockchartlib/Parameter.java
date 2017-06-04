package com.ly.stockchartlib;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by ly on 30/05/2017.
 */

public abstract class Parameter {
    protected KChartView kChartView;
    public Parameter(KChartView kChartView){
        this.kChartView = kChartView;
    }

    public abstract void setPaint(Paint paint);
    public abstract void calculate();
    public abstract Path getPath(int start);
    public float calculateY(float price){
        return kChartView.getYPosition(price);
    }
}
