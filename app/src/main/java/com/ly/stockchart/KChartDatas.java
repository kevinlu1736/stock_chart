package com.ly.stockchart;

import com.ly.stockchartlib.CandleStickData;

import java.util.ArrayList;

/**
 * Created by ly on 29/05/2017.
 */

public class KChartDatas {
    public static final int CHART_TYPE_DAY = 1;
    public static final int CHART_TYPE_WEEK = 2;
    public static final int CHART_TYPE_MONTH = 3;
    public static final int CHART_TYPE_HOUR = 4;
    public static final int CHART_TYPE_5MIN = 5;
    public static final int CHART_TYPE_MIN = 6;

    private int chartType = CHART_TYPE_DAY;
    private float priceSpan;

    private ArrayList<CandleStickData> datas = new ArrayList<>();

    public KChartDatas(ArrayList<CandleStickData> datas){
        this(datas, CHART_TYPE_DAY);
    }

    public KChartDatas(ArrayList<CandleStickData> datas, int chartType){
        this.datas = datas;
    }




    /**
     * getters & setters
     */

    public ArrayList<CandleStickData> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<CandleStickData> datas) {
        this.datas = datas;
    }

    public float getPriceSpan() {
        return priceSpan;
    }

    public void setPriceSpan(float priceSpan) {
        this.priceSpan = priceSpan;
    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }
}
