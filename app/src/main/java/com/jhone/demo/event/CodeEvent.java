package com.jhone.demo.event;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * Created by jhone on 2016/4/21.
 */
public class CodeEvent {

    public static final int FIT_VIEWFIND_IMAGE=1;
    public static final int SCAN_SUCCESS_LOCAL=2;
    public static final int SCAN_SUCCESS_CAMERA=3;


    private Bitmap bitmap;
    private Result result;
    private int type;
    public CodeEvent(Result result,Bitmap bitmap,int type) {
        this.result = result;
        this.bitmap=bitmap;
        this.type=type;
    }

    public Result getResult() {
        return result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getType() {
        return type;
    }
}
