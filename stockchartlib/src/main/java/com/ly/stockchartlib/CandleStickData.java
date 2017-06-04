package com.ly.stockchartlib;

/**
 * Created by ly on 29/05/2017.
 */

public class CandleStickData {
    private float maxPrice;
    private float minPrice;
    private float openingPrice;
    private float settlement;
    private float lastSettlement;
    private long startTimestamp;
    private long endTimestamp;

    public CandleStickData(float openingPrice, float settlement, float maxPrice, float minPrice){
        this.openingPrice = openingPrice;
        this.settlement = settlement;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(float openingPrice) {
        this.openingPrice = openingPrice;
    }

    public float getSettlement() {
        return settlement;
    }

    public void setSettlement(float settlement) {
        this.settlement = settlement;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public float getLastSettlement() {
        return lastSettlement;
    }

    public void setLastSettlement(float lastSettlement) {
        this.lastSettlement = lastSettlement;
    }
}
