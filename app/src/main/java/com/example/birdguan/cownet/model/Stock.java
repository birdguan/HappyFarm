package com.example.birdguan.cownet.model;

/**
 * Created by Gwg on 2016-10-24.
 */

public class Stock {
    private String mPartCode;
    private String mBarCode;
    private String mSellPrice;
    private String mName;
    private int mStockNum;
    private int mInventoryNum;
    private int mReplayNum;

    public Stock(String partCode, String barCode, String sellPrice, String name, int stockNum, int inventoryNum, int replayNum) {
        this.mPartCode = partCode;
        this.mBarCode = barCode;
        this.mSellPrice = sellPrice;
        this.mName = name;
        this.mStockNum = stockNum;
        this.mInventoryNum = inventoryNum;
        this.mReplayNum = replayNum;
    }

    public void setPartCode(String partCode) {
        this.mPartCode = partCode;
    }

    public String getPartCode() {
        return mPartCode;
    }

    public void setBarCode(String barCode) {
        this.mBarCode = barCode;
    }

    public String getBarCode() {
        return mBarCode;
    }

    public void setSellPrice(String sellPrice) {
        this.mSellPrice = sellPrice;
    }

    public String getSellPrice() {
        return mSellPrice;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setStockNum(int stockNum) {
        this.mStockNum = stockNum;
    }

    public int getStockNum() {
        return mStockNum;
    }

    public void setInventoryNum(int inventoryNum) {
        this.mInventoryNum = inventoryNum;
    }

    public int getInventoryNum() {
        return mInventoryNum;
    }

    public void setReplayNum(int replayNum) {
        this.mReplayNum = replayNum;
    }

    public int getReplayNum() {
        return mReplayNum;
    }
}
