package com.example.birdguan.cownet.utils;

public interface ProgressListener {
    //已完成的 总的文件长度 是否完成
    void onProgress(long currentBytes, long contentLength, boolean done);
}
