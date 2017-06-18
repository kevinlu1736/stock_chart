package com.ly.stockchartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ly on 29/05/2017.
 */

public class KChartView extends View{

    private int viewWidth;
    private int viewHeight;

    private float candleStickGap = dp2px(1);
    private float candleStickLineWidth = dp2px(1);
    private float startGap=0;
    private float endGap=0;

    private float candleStickWidth;
    private float displayHeightRatio = 0.9f;
    private float bottomPadding = dp2px(5);
    private float displayTop;
    private float displayBottom;

    // price/px
    private float displayDensity;

    private ArrayList<CandleStickData> datas = new ArrayList<>();
    private ArrayList<CandleStick> candleSticks = new ArrayList<>();

    private int candleStickNum;
    private float displayWidth;
    private float displayHeight;
    private float priceSpan;
    private float minPrice = Float.MAX_VALUE;
    private float maxPrice = Float.MIN_VALUE;

    private int colorRise = Color.parseColor("#ff0000");
    private int colorFall = Color.parseColor("#00ff00");
    private int colorUnchanged = Color.parseColor("#ffffff");
    private Paint paint;

    private ArrayList<Parameter> parameters = new ArrayList<>();
    private ArrayList<Parameter> defaultParams = new ArrayList<>();

    private int color_avg7 = Color.parseColor("#FFFD94FF");
    private int color_avg14 = Color.parseColor("#FFF7FF00");
    private int color_avg28 = Color.parseColor("#FF7AD5FC");
    private int color_avg60 = Color.parseColor("#ffffff");
    private float crossY = 0;
    private float crossX = 0;
    private int displayLeft;
    private int displayRight;
    private int lastCrossIndex = Integer.MIN_VALUE;
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener;
    private ScaleGestureDetector scaleGestureDetector;
    private int displayStart;
    private int displayEnd;
    private ArrayList<CandleStickData> totalList = new ArrayList<>();
    private double minScaleThreshold = 0.0618;

    public KChartView(Context context) {
        this(context, null);
    }

    public KChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setDatas(ArrayList<CandleStickData> datas, int displayStart, int displayEnd) {
        clearDatas();
        this.displayStart = displayStart;
        this.displayEnd = displayEnd;
        totalList = datas;

        initDatas();
        initDefaultParams();

        initListeners();
        notifyDataSetChanges();
    }

    private void initDatas(){
        this.datas.addAll(totalList.subList(displayStart, displayEnd+1));
        candleStickNum = this.datas.size();
        calculatePriceSpan();
    }

    private void setDisplayStart(int start){
        displayStart=start<0?0:start;
    }

    private void setDisplayEnd(int end){
        displayEnd=end>(totalList.size()-1)?totalList.size()-1:end;
    }



    private void reinitiateDatas() {
        datas.clear();
        initDatas();

        if(datas.size()>0){
            getDisplayDensity();
            getCandleStickWidth();
        }

        notifyDataSetChanges();
    }

    public void moveChart(int num){
        if(((displayStart+num)<0)||((displayEnd+num) > (totalList.size()-1)))return;
        setDisplayStart(displayStart+num);
        setDisplayEnd(displayEnd+num);
        reinitiateDatas();
    }


    private void initListeners(){
        onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            int lastDisplayNum = candleStickNum;
            int centerIndex;
//            int i=0;
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (calculateScaledDatas(detector)) return false;

                reinitiateDatas();

//                float deltaX = detector.getCurrentSpanX()-detector.getPreviousSpanX();
//                Toast.makeText(getContext(), "scale"+i, Toast.LENGTH_SHORT).show();
//                i+=1;
                return true;
            }

            private boolean calculateScaledDatas(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                if(Math.abs(scaleFactor-1)< minScaleThreshold)return true;
                int displayNum = (int)(candleStickNum/scaleFactor+0.5f);



                //3 sticks at least
                displayNum = displayNum<=2?2:displayNum;

                if(displayNum==lastDisplayNum) return true;
                lastDisplayNum = displayNum;

                displayStart = centerIndex-displayNum/2;
                displayEnd = centerIndex+(displayNum-displayNum/2);

                displayStart=displayStart<0?0:displayStart;
                displayEnd=displayEnd>(totalList.size()-1)?totalList.size()-1:displayEnd;

                int actNum = displayEnd-displayStart+1;
                if(actNum<displayNum&&displayStart==0){
                    displayEnd+=displayNum-actNum;
                    displayEnd=displayEnd>(totalList.size()-1)?totalList.size()-1:displayEnd;
                }
                else if(actNum<displayNum&&displayEnd==(totalList.size()-1)){
                    displayStart-=displayNum-actNum;
                    displayStart=displayStart<0?0:displayStart;
                }
//                System.out.println("begin scale center: " + centerIndex + "|start: " + displayStart + "|end: "+displayEnd);
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                if((displayStart+displayEnd)%2==0){
                    centerIndex = (displayStart+displayEnd)/2;
                }
                else{
                    centerIndex = (displayStart+displayEnd)/2+1;
                }
//                System.out.println("begin scale center: " + centerIndex);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }

        };
        scaleGestureDetector = new ScaleGestureDetector(getContext(), onScaleGestureListener);
    }

    public void initDefaultParams(){
        MovingAverage movingAverage_7day = new MovingAverage(this, 7);
        movingAverage_7day.setColor(color_avg7);
        movingAverage_7day.calculate();
        MovingAverage movingAverage_14day = new MovingAverage(this, 14);
        movingAverage_14day.setColor(color_avg14);
        movingAverage_14day.calculate();
        MovingAverage movingAverage_28day = new MovingAverage(this, 28);
        movingAverage_28day.setColor(color_avg28);
        movingAverage_28day.calculate();
        MovingAverage movingAverage_60day = new MovingAverage(this, 60);
        movingAverage_60day.setColor(color_avg60);
        movingAverage_60day.calculate();

        defaultParams.add(movingAverage_7day);
        defaultParams.add(movingAverage_14day);
        defaultParams.add(movingAverage_28day);
        defaultParams.add(movingAverage_60day);

        parameters.addAll(defaultParams);
    }


    public void notifyDataSetChanges(){
        invalidate();
    }


    private void initPaint(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        candleSticks.clear();
        paint.setStyle(Paint.Style.FILL);
        for(int i=0;i<datas.size();i++){
            CandleStick candleStick = new CandleStick(datas.get(i), i);
            candleSticks.add(candleStick);
            paint.setColor(candleStick.getCandleStickColor());
            canvas.drawPath(candleStick.getCandlePath(), paint);
        }

        for(Parameter parameter: parameters){
            parameter.setPaint(paint);
            canvas.drawPath(parameter.getPath(displayStart), paint);
        }

        drawCrossLine(canvas);
    }

    private void drawCrossLine(Canvas canvas){
        if(crossX==0&&crossY==0)return;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(1));
        paint.setColor(Color.parseColor("#ffffff"));
        Path path = new Path();
        path.moveTo(crossX, displayTop);
        path.lineTo(crossX, displayBottom);
        path.moveTo(displayLeft, crossY);
        path.lineTo(displayRight, crossY);

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        displayWidth = viewWidth-startGap-endGap;
        displayHeight = viewHeight*displayHeightRatio;
        displayBottom = viewHeight-bottomPadding;
        displayTop = displayBottom-displayHeight;
        displayLeft = 0;
        displayRight = viewWidth;

        //if 5dp is to much for bottom padding, rare case
        if(displayTop<0){
            displayTop = 0;
            displayHeight = displayBottom - displayTop;
        }

        if(datas.size()>0){
            getDisplayDensity();
            getCandleStickWidth();
        }
    }

    private void getDisplayDensity() {
        displayDensity = priceSpan/displayHeight;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);
        if(scaleGestureDetector.isInProgress())return true;

        int action = event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            showCrossLine(event.getX());
        }else if(action==MotionEvent.ACTION_MOVE){
            showCrossLine(event.getX());
        }else if(action==MotionEvent.ACTION_CANCEL){

        }else if(action==MotionEvent.ACTION_UP){

        }

        return true;
    }


    public interface OnKChartFocusChange{
        void onKChartFocusChange(CandleStickData candleStickData);
    }

    private OnKChartFocusChange onKChartFocusChange;

    public void setOnKChartFocusChange(OnKChartFocusChange onKChartFocusChange) {
        this.onKChartFocusChange = onKChartFocusChange;
    }

    private void showCrossLine(float moveX){
        if(moveX<=startGap||moveX>=displayRight-endGap)return;
        int curIndex = findCandleStick(moveX);
        //more effective only redraw on index change
        if(curIndex==lastCrossIndex||curIndex>=datas.size())return;

        if(onKChartFocusChange!=null){
            onKChartFocusChange.onKChartFocusChange(datas.get(curIndex));
        }

        crossY = candleSticks.get(curIndex).getSettlement();
        crossX = candleSticks.get(curIndex).centerX;
        invalidate();
    }

    private int findCandleStick(float x){
        return (int)((x-startGap)/(candleStickWidth+candleStickGap));
    }



    public class CandleStick{
        float lineTop;
        float lineBottom;
        float candleTop;
        float candleBottom;
        float centerX;
        float settlement;
        int candleStickColor;
        int index;
        CandleStickData candleStickData;
        Path candlePath;

        public CandleStick(CandleStickData candleStickData, int index){
            this.candleStickData = candleStickData;
            this.index = index;
            initCandleDatas();
        }

        private void initCandleDatas(){
            centerX = startGap + (candleStickWidth + candleStickGap)*index + candleStickWidth/2;
            lineTop = getYPosition(candleStickData.getMaxPrice());
            lineBottom = getYPosition(candleStickData.getMinPrice());
            candleTop = getYPosition(Math.max(candleStickData.getOpeningPrice(), candleStickData.getSettlement()));
            candleBottom = getYPosition(Math.min(candleStickData.getOpeningPrice(), candleStickData.getSettlement()));

            candleTop=Math.abs(candleTop-candleBottom)<2?(candleBottom-2):candleTop;

            settlement = candleStickData.getOpeningPrice()>candleStickData.getSettlement()?candleBottom:candleTop;


            if(candleStickData.getOpeningPrice()<candleStickData.getSettlement()){
                candleStickColor = colorRise;
            }else if(candleStickData.getOpeningPrice()>candleStickData.getSettlement()){
                candleStickColor = colorFall;
            }else{
                candleStickColor = colorUnchanged;
            }

            genPath();
        }




        void genPath(){
            Path path = new Path();
            path.addRect(getCandleBodyRect(), Path.Direction.CCW);
            path.addRect(getCandleLine(), Path.Direction.CCW);
            this.candlePath = path;
        }

        RectF getCandleBodyRect(){
            return new RectF(centerX-candleStickWidth/2, candleTop, centerX+candleStickWidth/2, candleBottom);
        }

        RectF getCandleLine(){
            return new RectF(centerX-candleStickLineWidth/2, lineTop, centerX+candleStickLineWidth/2, lineBottom);
        }

        public Path getCandlePath() {
            return candlePath;
        }

        public int getCandleStickColor() {
            return candleStickColor;
        }

        public float getSettlement() {
            return settlement;
        }
    }



    /**
     * helpers
     */
    private void calculatePriceSpan() {
        for(int i=0;i<datas.size();i++){
            maxPrice = datas.get(i).getMaxPrice()> maxPrice ?datas.get(i).getMaxPrice(): maxPrice;
            minPrice = datas.get(i).getMinPrice()< minPrice ?datas.get(i).getMinPrice(): minPrice;
        }
        priceSpan = maxPrice - minPrice;
    }

    public float getYPosition(float price){
        float deltaPrice = price - minPrice;
        float deltaHeight = deltaPrice/displayDensity;
        return displayBottom-deltaHeight;
    }

    private void clearDatas() {
        parameters.clear();
        datas.clear();
        candleSticks.clear();
        totalList.clear();
    }

    private float dp2px(float dp){
        return getResources().getDisplayMetrics().density*dp;
    }

    private float getCandleStickWidth() {
        float candlesWidth = displayWidth-candleStickGap*(candleStickNum-1);
        if(candlesWidth<=candleStickNum){
            //TODO not enough space to display
            return 0;
        }

        candleStickWidth = candlesWidth/candleStickNum;
        return candleStickWidth;
    }

    /**
     * getters & setters
     */

    public ArrayList<CandleStickData> getDatas() {
        return datas;
    }

    public int getColorRise() {
        return colorRise;
    }

    public void setColorRise(int colorRise) {
        this.colorRise = colorRise;
    }

    public int getColorFall() {
        return colorFall;
    }

    public void setColorFall(int colorFall) {
        this.colorFall = colorFall;
    }

    public int getColorUnchanged() {
        return colorUnchanged;
    }

    public void setColorUnchanged(int colorUnchanged) {
        this.colorUnchanged = colorUnchanged;
    }

    public ArrayList<CandleStick> getCandleSticks() {
        return candleSticks;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter){
        parameters.add(parameter);
    }

    public void removeParameter(Parameter parameter){
        parameters.remove(parameter);
    }

    public void clearParameters(){
        parameters.clear();
    }

    public int getColor_avg7() {
        return color_avg7;
    }

    public void setColor_avg7(int color_avg7) {
        this.color_avg7 = color_avg7;
    }

    public int getColor_avg14() {
        return color_avg14;
    }

    public void setColor_avg14(int color_avg14) {
        this.color_avg14 = color_avg14;
    }

    public int getColor_avg28() {
        return color_avg28;
    }

    public void setColor_avg28(int color_avg28) {
        this.color_avg28 = color_avg28;
    }

    public int getColor_avg60() {
        return color_avg60;
    }

    public void setColor_avg60(int color_avg60) {
        this.color_avg60 = color_avg60;
    }

    public ArrayList<CandleStickData> getTotalList() {
        return totalList;
    }
}
