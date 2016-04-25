package com.jhone.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.yolanda.nohttp.Logger;

/**
 * Created by jhone on 2016/4/25.
 */
public class DampLinearLayout extends LinearLayout {

    public DampLinearLayout(Context context) {
        this(context, null);
    }

    public DampLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private LayoutParams params;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        params= (LayoutParams) getLayoutParams();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                Logger.v("move   "+params.topMargin);
                params.topMargin+=10;
                setLayoutParams(params);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                params.topMargin=0;
                setLayoutParams(params);
                invalidate();
                break;
        }

       return super.onTouchEvent(event);
    }
}
