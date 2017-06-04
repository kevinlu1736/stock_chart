package com.ly.stockchartlib;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by ly on 30/05/2017.
 */
public class MovingAverage extends Parameter{
    private int interval;
    private ArrayList<Float> averages = new ArrayList<>();
    private Path movingAveragePath;
    private int color;


    public MovingAverage(KChartView kChartView, int interval){
        super(kChartView);
        this.interval = interval;
    }

    @Override
    public void calculate() {
        ArrayList<CandleStickData> datas = kChartView.getTotalList();
        averages.clear();

        float sum = 0;
        float average;
        for(int i=0;i<datas.size();i++){
            if(i<interval){
                sum+=datas.get(i).getSettlement();
                average = sum/(i+1);
            }
            else{
                sum =  sum - datas.get(i-interval).getSettlement() + datas.get(i).getSettlement();
                average = sum/interval;
            }
            averages.add(average);
        }
    }

    @Override
    public Path getPath(int start) {
        ArrayList<KChartView.CandleStick> candleSticks = kChartView.getCandleSticks();
        movingAveragePath = new Path();

        for(int i=0;i<candleSticks.size();i++){
            if(i>0){
                movingAveragePath.moveTo(candleSticks.get(i-1).centerX, calculateY(averages.get(start+i-1)));
                movingAveragePath.lineTo(candleSticks.get(i).centerX, calculateY(averages.get(start+i)));
            }
        }
        return movingAveragePath;
    }

    @Override
    public void setPaint(Paint paint){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(1));
        paint.setColor(getColor());
    }

    private float dp2px(float dp){
        return dp*kChartView.getResources().getDisplayMetrics().density;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
