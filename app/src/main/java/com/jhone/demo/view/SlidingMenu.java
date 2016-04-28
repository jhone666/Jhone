package com.jhone.demo.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.jhone.demo.R;
import com.jhone.demo.utils.DeviceUtil;
import com.nineoldandroids.view.ViewHelper;
import com.yolanda.nohttp.Logger;

/**
 * Created by jhone on 2016/4/26.
 */
public class SlidingMenu extends HorizontalScrollView {
    private LinearLayout mWapper;// 自定义控件中的唯一子view
    /**
     * mMenu和mContent都是放在mWapper中的，且mWapper方向是Horizontal
     */
    private ViewGroup mMenu;// 菜单
    private ViewGroup mContent;// 内容

    private int mScreenWidth;// 屏幕width

    private int mMenuWidth;// 菜单width

    private int mMenuRightPadding = 0;
    private int  filging;

    private boolean once;

    public boolean isOpen;
    private boolean showAnim;

    private Context context;

    private float Ax;
    private float Ay;
    private float Bx;
    private float By;
    private List<View> ignoredViews = new ArrayList<View>();

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        // 1、处理android属性
        super(context, attrs, defStyle);

        this.context = context;
        // 2、处理自定义属性
        // 取出布局中的android属性与自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SlidingMenu, defStyle, 0);

        // 得到属性个数
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    WindowManager wm = (WindowManager) context
                            .getSystemService(Context.WINDOW_SERVICE);
                    // DisplayMetrics 屏幕量度对象， 包含屏幕的一些信息，如 宽 高 密度等
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(outMetrics);
                    mScreenWidth = outMetrics.widthPixels;
                    mMenuRightPadding= (int) (a.getFraction(attr,1,1, (float) 0.25)* mScreenWidth);
                    break;
                case R.styleable.SlidingMenu_showAnim:
                    showAnim=a.getBoolean(attr,false);
                    break;
            }
        }
        // 回收耗资源对象
        a.recycle();
        filging=ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    /**
     * 初始化子view和自定义view本身的尺寸大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置只测量一次
        if (!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
                    - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始布局(会多次调用)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // extends scrollview的方法，scrollTo 正数：滚动条向右滑，界面向左移动
            this.scrollTo(mMenuWidth, 0);
        }
    }

    float start = 0f;
    float end = 0f;
    private Rect rect;
    boolean down=false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        createVelocityTracker(ev);
        switch (ev.getAction()) {
//            不能在这里获取start，因为当子控件设置onClick事件后，不会在调用down事件
//            case MotionEvent.ACTION_DOWN:
//                Logger.d("ACTION_DOWN");
//                start = ev.getX();
//                break;
            case MotionEvent.ACTION_MOVE:
                if (!down){
                    down=true;
                    start=ev.getX();
                }
                end = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                down=false;
                int scrollX = getScrollX();
                if (filging<= getScrollVelocity()) {// 响应快速左滑和右滑打开或关闭菜单
                    if (isOpen&&start>end) {
                        closeMenu();
                    } else if (!isOpen&&start<end){
                        openMenu();
                    }
                    recycleVelocityTracker();
                    return true;
                }else{
                    if (isOpen&&start==end) {// 当菜单打开时点击菜单以外的区域关闭菜单
                        rect=new Rect();
                        mContent.getGlobalVisibleRect(rect);
                        if (rect.contains(((int)ev.getX()),((int)ev.getY()))) {
                            closeMenu();
                            return true;
                        }
                    }
                    if (scrollX >= mMenuWidth / 2) {
                        this.smoothScrollTo(mMenuWidth, 0);
                        isOpen = false;
                    } else {
                        this.smoothScrollTo(0, 0);
                        isOpen = true;
                    }
                    return true;
                }
        }
        return super.onTouchEvent(ev);
    }


    private VelocityTracker mVelocityTracker;

    /**
     * 创建速率追踪者
     */
    private void createVelocityTracker(MotionEvent event) {
        if(this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
    }

    /**
     * @return X轴方向的速率
     */
    private int getScrollVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int)this.mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    private void recycleVelocityTracker() {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    public void addIgnoredView(View v) {
        ignoredViews.add(v);
    }

    public void removeIgnoredView(View v) {
        ignoredViews.remove(v);
    }

    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : ignoredViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    // 拦截Touch事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        float pointX = ev.getX();
        float pointY = ev.getY();

        if (isOpen && pointX > mMenuWidth) {// 当open时，不监听内容区域的事件
            return true;
        }
        // //通过传入的px值设置子view的传递事件
        // if(!isOpen&&pointX>Ax&&pointX<Bx&&pointY>Ay&&pointY<By){
        // return false;//传递给子view处理
        // }
        // 通过设置的view对象，设置view的传递事件
        if (!isOpen && isInIgnoredView(ev)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    // 分发Touch事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.dispatchTouchEvent(ev);
    }

    public void openMenu() {
        if (isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    public void closeMenu() {
        if (!isOpen)
            return;
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private boolean visi = true;

    @Override
    protected void onScrollChanged(int hscroll, int t, int oldl, int oldt) {
        super.onScrollChanged(hscroll, t, oldl, oldt);
        float scale = hscroll * 1.0f / mMenuWidth; // 1 ~ 0 l不是1

        float rightScale = 0.7f + 0.3f * scale;// 1.0 ~ 0.7
        float leftScale = 1.0f - scale * 0.3f;// 0.7 ~ 1.0
        float leftAlpha = 0.6f + 0.4f * (1 - scale);// 0.6 ~ 1.0

        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);// 0.7~菜单和内容的比例7：3

        if (showAnim){
            ViewHelper.setScaleX(mMenu, leftScale);
            ViewHelper.setScaleY(mMenu, leftScale);
            ViewHelper.setAlpha(mMenu, leftAlpha);
            ViewHelper.setPivotX(mContent, 0);
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            ViewHelper.setScaleX(mContent, rightScale);
            ViewHelper.setScaleY(mContent, rightScale);
        }
//        if (scale == 1) {
////            mMenu.setVisibility(View.INVISIBLE);// 当菜单关闭时，可能在mContent页面可以点到菜单上的控件
////            visi = true;
//            mMenu.setClickable(false);
//        } else {
////            if (visi) {
////                mMenu.setVisibility(View.VISIBLE);
////                visi = false;
////            }
//            mMenu.setClickable(true);
//        }
    }

}
