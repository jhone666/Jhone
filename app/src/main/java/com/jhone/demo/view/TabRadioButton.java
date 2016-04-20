package com.jhone.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.jhone.demo.R;

/**
 * Created by jhone on 2016/4/20.
 * 调整drawable图片大小
 */
public class TabRadioButton extends RadioButton {

    private int iconWidth=30,iconHeight=30;

    public TabRadioButton(Context context) {
        this(context,null);
    }

    public TabRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TabRadioButton, defStyleAttr, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TabRadioButton_drawableWidth:
                    iconWidth =a.getDimensionPixelSize(attr, 30);
                    break;
                case R.styleable.TabRadioButton_drawableHeight:
                    iconHeight =a.getDimensionPixelSize(attr, 30);
                    break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable[] drawables = getCompoundDrawables();
        Rect rect=new Rect(0,0,iconWidth,iconHeight);
        for (int i=0;i<drawables.length;i++){
            if (drawables[i]!=null)
            drawables[i].setBounds(rect);
        }
        setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);
        super.onDraw(canvas);
    }
}
