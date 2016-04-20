package com.jhone.demo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.jhone.demo.utils.DeviceUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by jhone on 2016/4/19.
 */
public class SlideMenu extends HorizontalScrollView {
    private final int mScreenWidth;
    private final int mMenuRightPadding;
    private int mMenuWidth;
    private boolean isOpen;
    private boolean once;
    private boolean showAnim;
    private int mHalfMenuWidth;
    public static final int SNAP_VELOCITY = 270;
    private VelocityTracker mVelocityTracker;
    private float prevX;
    private float maxX;
    private float minX;
    private SlideMenu.OnScrollProgressListener progressListener;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    public SlideMenu(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.prevX = 0.0F;
        this.maxX = 0.0F;
        this.minX = 0.0F;
        this.mScreenWidth = DeviceUtil.getWidth(context);
        this.mMenuRightPadding = (int) ((double) this.mScreenWidth * 0.27D);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.once) {
            LinearLayout wrapper = (LinearLayout) this.getChildAt(0);
            this.mMenu = (ViewGroup) wrapper.getChildAt(0);
            this.mContent = (ViewGroup) wrapper.getChildAt(1);
            this.mMenuWidth = this.mScreenWidth - this.mMenuRightPadding;
            this.mHalfMenuWidth = this.mMenuWidth / 3;
            this.mMenu.getLayoutParams().width = this.mMenuWidth;
            this.mContent.getLayoutParams().width = this.mScreenWidth;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(this.mMenuWidth, 0);
            this.once = true;
        }

    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.showAnim) {
            float scale = (float) l * 1.0F / (float) this.mMenuWidth;
            float leftScale = 1.0F - 0.3F * scale;
            float rightScale = 0.8F + scale * 0.2F;
            ViewHelper.setScaleX(this.mMenu, leftScale);
            ViewHelper.setScaleY(this.mMenu, leftScale);
            ViewHelper.setAlpha(this.mMenu, 0.6F + 0.4F * (1.0F - scale));
            ViewHelper.setTranslationX(this.mMenu, (float) this.mMenuWidth * scale * 0.7F);
            ViewHelper.setPivotX(this.mContent, 0.0F);
            ViewHelper.setPivotY(this.mContent, (float) (this.mContent.getHeight() / 2));
            ViewHelper.setScaleX(this.mContent, rightScale);
            ViewHelper.setScaleY(this.mContent, rightScale);
        }
    }

    private int preX,preY;
    private Rect rect;
    public boolean onTouchEvent(MotionEvent ev) {
        this.createVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX= (int) ev.getX();
                preY= (int) ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (this.getScrollVelocity() > 270) {
                    this.checkMenuByOrientation(this.prevX - this.maxX, this.prevX - this.minX);
                } else {
                    this.checkMenuByDistance();
                }

                this.maxX = this.minX = this.prevX = 0.0F;
                this.recycleVelocityTracker();

//                if (isOpen()){
//                    rect=new Rect();
//                    mContent.getGlobalVisibleRect(rect);
//                    if (rect.contains(preX,preY)&&rect.contains(((int)ev.getX()),((int)ev.getY()))){
//                        close();
//                    }
//                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                if (this.prevX == 0.0F) {
                    this.maxX = this.minX = this.prevX = x;
                }

                if (this.maxX < x) {
                    this.maxX = x;
                }

                if (this.minX > x) {
                    this.minX = x;
                }
            default:
        }
                return super.onTouchEvent(ev);
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (this.progressListener != null) {
            this.progressListener.onProgress(scrollX, this.mMenuWidth);
        }

    }

    private void createVelocityTracker(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

        this.mVelocityTracker.addMovement(event);
    }

    private int getScrollVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) this.mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    private void recycleVelocityTracker() {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    private void checkMenuByOrientation(float maxMove, float minMove) {
        float availValue = Math.abs(maxMove) > Math.abs(minMove) ? maxMove : minMove;
        if (availValue > 0.0F) {
            this.closeMenu();
        } else {
            this.openMenu();
        }

    }

    private void checkMenuByDistance() {
        if (this.getScrollX() > this.mHalfMenuWidth) {
            this.close();
        } else {
            this.open();
        }

    }

    private void open() {
        this.smoothScrollTo(0, 0);
        this.isOpen = true;
    }

    private void close() {
        this.smoothScrollTo(this.mMenuWidth, 0);
        this.isOpen = false;
    }

    public void openMenu() {
        if (!this.isOpen) {
            this.open();
        }

    }

    public void closeMenu() {
        if (this.isOpen) {
            this.close();
        }

    }

    public void toggleMenu() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }

    }

    public boolean isShowAnim() {
        return this.showAnim;
    }

    /**
     * 设置是否使用动画
     *
     * @param showAnim
     */
    public void setShowAnim(boolean showAnim) {
        this.showAnim = showAnim;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOnScrollProgressListener(SlideMenu.OnScrollProgressListener l) {
        this.progressListener = l;
    }

    public interface OnScrollProgressListener {
        void onProgress(int var1, int var2);
    }
}
