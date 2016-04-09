package com.jhone.demo.utils;

import android.widget.Toast;

import com.jhone.demo.common.AppApplication;


/**
 * Created by Administrator on 2016/3/18.
 * Description
 */
public class ToastUtils {


    public static void showShort(String message){
        Toast.makeText(AppApplication.getInstance().getContext(),message,Toast.LENGTH_SHORT).show();
    }
    public static void showLong(String message){
        Toast.makeText(AppApplication.getInstance().getContext(),message,Toast.LENGTH_SHORT).show();
    }
}
