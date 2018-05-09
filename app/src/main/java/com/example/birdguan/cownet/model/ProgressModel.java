package com.example.birdguan.cownet.model;

/**
 * Created by Gwg on 2016/8/31.
 */
public class ProgressModel {
    private long mCurrentBytes;
    private long mContentLength;
    private boolean mIsDone;

    public ProgressModel(long currentBytes, long contentLength, boolean isDone) {
        this.mCurrentBytes = currentBytes;
        this.mContentLength = contentLength;
        this.mIsDone = isDone;
    }

    public void setCurrentBytes(long currentBytes) {
        this.mCurrentBytes = currentBytes;
    }

    public long getCurrentBytes() {
        return mCurrentBytes;
    }

    public void setShopName(long contentLength) {
        this.mContentLength = contentLength;
    }

    public long getContentLength() {
        return mContentLength;
    }

    public boolean isDone() {
        return mIsDone;
    }
}
