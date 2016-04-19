package com.jhone.demo.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import java.util.ArrayList;


public class AppApplication extends Application {

    private static AppApplication application;
    private static Context context;

    public AppApplication() {
        application = this;
    }
    /*
     * @author jhone
     * @create at 2016/3/18 16:33
     * 避免AppApplication在进入APP时保存的对象为空
     */
    public static synchronized AppApplication getInstance() {
        if (application == null) {
            application = new AppApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        NoHttp.init(this);
        Logger.setTag("jhone");
        Logger.setDebug(true);// 打开noHttp框架日志
    }

    public Context getContext(){
        return context;
    }

    /**
     * 保存打开未关闭的activity
     */
    public ArrayList<Activity> activities=new ArrayList<Activity>();

    public void exitApp(){
        if(activities.size()!=0){
            for (int i=0;i<activities.size();i++){
                if(activities.get(i)!=null){
                    activities.get(i).finish();
                    System.gc();
                }
            }
        }
    }

    /**
     * 判断是否登入状态
     * @return
     */
    public boolean isLogin(){
//        return SharedPrefsUtil.getValue(context,Constants.JUNLONG_USERINFO,Constants.ACCOUNT,null)!=null;
        return false;
    }
}