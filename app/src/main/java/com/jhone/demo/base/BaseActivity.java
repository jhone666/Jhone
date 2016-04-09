package com.jhone.demo.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jhone.demo.R;
import com.jhone.demo.utils.GestureBackUtil;
import com.jhone.demo.utils.SystemBarTintManager;


/**
 * Created by jhone on 2016/3/14.
 */
public class BaseActivity extends AppCompatActivity {

    private ImageView back; // 返回
    private TextView title;// 标题
    private TextView rightAction;
    private LinearLayout viewGroup;
    private Toast toast;
    private GestureBackUtil gestureBackUtil;

    private boolean isConsumed;// 事件是否消费掉


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(false);
            tintManager.setStatusBarTintResource(R.color.status_bar);
        }
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        isConsumed = false;

        initBaseView();
    }


    /**
     * @param isConsumed 设置事件是否消费掉不做手势返回判断，false-可以手势   true-不可以手势
     */
    public void setConsumed(boolean isConsumed) {
        this.isConsumed = isConsumed;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isConsumed && gestureBackUtil.onTouchEvent(ev)) {
            return true;
        }
        isConsumed = false;
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return isConsumed;
    }

    /**
     * 设置包含viewpager界面的手势返回上时判断pager的返回
     */
    public void setIgnoreViewPage(View view) {
        gestureBackUtil.setIgnoreViewPager(view);
    }

    private void initBaseView() {
        viewGroup = (LinearLayout) View.inflate(this, R.layout.base_bar, null);
        back = (ImageView) viewGroup.findViewById(R.id.back);
        title = (TextView) viewGroup.findViewById(R.id.title);
        rightAction = (TextView) viewGroup.findViewById(R.id.right_action);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setBarIsvisible(boolean isvisible) {
        if (viewGroup == null) return;
        viewGroup.getChildAt(0).setVisibility(isvisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置界面标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        this.title.setText(title);
    }

    protected TextView getTitleView() {
        return title;
    }

    protected void setActionBarBackunable() {
        back.setVisibility(View.INVISIBLE);
    }

    public void showActionBarRightView(View.OnClickListener clickListener) {
        this.showActionBarRightView(0, clickListener);
    }

    public void setBarRightViewBackground(int resid) {
        Drawable drawable = getResources().getDrawable(resid);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        rightAction.setCompoundDrawables(drawable, null, null, null);
        rightAction.setText("");//只显示图标不显示文字
        rightAction.setVisibility(View.VISIBLE);
    }

    public void showActionBarRightView(int textResid,
                                       View.OnClickListener clickListener) {
        if (textResid != 0) {
            rightAction.setCompoundDrawables(null, null, null, null);
            rightAction.setText(textResid);
        }
        rightAction.setVisibility(View.VISIBLE);
        rightAction.setOnClickListener(clickListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = View.inflate(this, layoutResID, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        contentView.setLayoutParams(layoutParams);
        gestureBackUtil = new GestureBackUtil(this);
        viewGroup.addView(contentView);
        super.setContentView(viewGroup);
    }

    @Override
    public void setContentView(View view) {
        View contentView = view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        contentView.setLayoutParams(layoutParams);
        viewGroup.addView(contentView);
        super.setContentView(viewGroup);
    }



    /**
     * 以LENGTH_SHORT显示toast
     *
     * @param msg
     */
    public void showToast(String msg) {
        toast.setText(msg);
        toast.show();
    }

    /**
     * 以LENGTH_LONG时间显示toast
     *
     * @param msg
     */
    public void showToastLong(String msg) {
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }

    /**
     * 显示退出确认对话框
     */
    public void showBackDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("友情提示").setMessage("您还有数据未提交,确定要离开页面?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

    }

    public TextView getRightAction() {
        return rightAction;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    /**
     * 改变当前内容视图
     * @param layoutId
     */
    public  void changeContent(int layoutId){
        viewGroup.removeView(viewGroup.getChildAt(1));
        View contentView = View.inflate(this, layoutId, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        contentView.setLayoutParams(layoutParams);
        viewGroup.addView(contentView);
    }

    public void changeContent(View view){
        viewGroup.removeView(viewGroup.getChildAt(1));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
    }
    /**
     * 设置无网络情况下视图
     */
    public  void setContentViewNetError(){
//        changeContent(R.layout.network_error);
    }

    /**
     * 进入页面无数据状态
     */
    public void setContentViewNoData(){
//        changeContent(R.layout.no_data);
    }

}
