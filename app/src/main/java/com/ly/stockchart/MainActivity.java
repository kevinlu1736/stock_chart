package com.ly.stockchart;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ly.stockchartlib.CandleStickData;
import com.ly.stockchartlib.KChartView;
import com.ly.stockchartlib.Widgets.TriStrokeBtn;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TriStrokeBtn.OnPressListener{

    @BindView(R.id.kchart)
    KChartView kChartView;

    @BindView(R.id.opening)
    TextView openingText;

    @BindView(R.id.settlement)
    TextView settlementText;

    @BindView(R.id.max)
    TextView maxText;

    @BindView(R.id.min)
    TextView minText;

    @BindView(R.id.right_arrow)
    TriStrokeBtn rightArrow;

    @BindView(R.id.left_arrow)
    TriStrokeBtn leftArrow;

    private ArrayList<CandleStickData> datas;
    private Random random;
    private int moveInterval = 3;

    public static WeakReference<KChartView> kChart;
    private MoveThread moveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        genTestDatas(200);

        kChartView.setDatas(datas, 75, 125);

        kChartView.setOnKChartFocusChange(new KChartView.OnKChartFocusChange() {
            @Override
            public void onKChartFocusChange(CandleStickData candleStickData) {
                minText.setText("最低："+String.valueOf(round2(candleStickData.getMinPrice())));
                setTextColor(minText, candleStickData.getLastSettlement(), candleStickData.getMinPrice());

                maxText.setText("最高："+String.valueOf(round2(candleStickData.getMaxPrice())));
                setTextColor(maxText, candleStickData.getLastSettlement(), candleStickData.getMaxPrice());

                settlementText.setText("收盘："+String.valueOf(round2(candleStickData.getSettlement())));
                setTextColor(settlementText, candleStickData.getLastSettlement(), candleStickData.getSettlement());

                openingText.setText("开盘："+String.valueOf(round2(candleStickData.getOpeningPrice())));
                setTextColor(openingText, candleStickData.getLastSettlement(), candleStickData.getOpeningPrice());
            }
        });
        kChart = new WeakReference<>(kChartView);

        rightArrow.setDirection(TriStrokeBtn.DIRECTION_RIGHT);
        leftArrow.setOnPressListener(this);
        rightArrow.setOnPressListener(this);
    }

    private void setTextColor(TextView tv, float lastPrice, float curPrice){
        int trend = curPrice>lastPrice?1:(curPrice==lastPrice?0:-1);

        tv.setTextColor(trend==0? Color.WHITE:(trend==1?Color.RED:Color.GREEN));

    }

    private float round2(float price){
        return ((int)(price*100+0.5f))/100f;
    }



    private void genTestDatas(int num){
        datas = new ArrayList<>();
        CandleStickData lastCandleData = new CandleStickData(10f, 10.86f, 10.97f, 9.84f);
        for(int i=0;i<num;i++){
            CandleStickData curCandleData = genTestCandle(lastCandleData);
            curCandleData.setLastSettlement(i>0?lastCandleData.getSettlement():0);
            datas.add(curCandleData);
            lastCandleData = curCandleData;
        }
    }

    private CandleStickData genTestCandle(CandleStickData lastCandleData){
        random = new Random();
        float openingPrice = genRandomPrice(lastCandleData.getSettlement());
        float settlement = genRandomPrice(lastCandleData.getSettlement());
        float maxPrice = genMaxPrice(lastCandleData.getSettlement(), Math.max(openingPrice, settlement));
        float minPrice = genMinPrice(lastCandleData.getSettlement(), Math.min(openingPrice, settlement));
        return new CandleStickData(openingPrice, settlement, maxPrice, minPrice);
    }

    private float genMaxPrice(float lastPrice, float curMax){
        float highestBound = lastPrice*1.1f;
        float delta = highestBound -curMax;
        return highestBound - random.nextFloat()*delta;
    }

    private float genMinPrice(float lastPrice, float curMin){
        float lowestBound = lastPrice*0.9f;
        float delta = curMin - lowestBound;
        return lowestBound + random.nextFloat()*delta;
    }

    private float genRandomPrice(float lastPrice){
        return lastPrice*(random.nextFloat()*0.2f+0.9f);
    }


    static class MoveThread extends Thread{
        private int num;
        private boolean needMove=true;

        public MoveThread(int num){
            this.num = num;
        }

        @Override
        public void run() {
                    while (getNeedMove()){
                        try {
                            sleep(200);
                            final KChartView kc = kChart.get();
                            if(kc!=null){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                    kc.moveChart(num);

                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


        }

        public synchronized void setNeedMove(boolean needMove) {
            this.needMove = needMove;
        }

        public synchronized boolean getNeedMove(){
            return this.needMove;
        }
    };



    @Override
    public void onPressEnd(int id) {
        if(moveThread==null)return;
        moveThread.setNeedMove(false);
    }

    @Override
    public void onPressStart(int id) {
        if(moveThread!=null){
            moveThread.setNeedMove(false);
        }
        moveThread = new MoveThread(id==R.id.left_arrow?-moveInterval:moveInterval);
        moveThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        moveThread.setNeedMove(false);
    }
}
