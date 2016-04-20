package com.jhone.demo.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhone.demo.R;
import com.jhone.demo.utils.DeviceUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by jhone on 2016/4/19.
 */
public class SplashActivity extends AppCompatActivity {

    private TextView tv_one,tv_two,tv_three,tv_four;
    private ImageView iv_loading;
    private  ObjectAnimator animaOne,animatwo,animathree,animafour,animaloading;
    private int screenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        showAnima();
    }

    private void showAnima() {
        screenWidth= DeviceUtil.getWidth(this);
        animaOne = ObjectAnimator.ofFloat(tv_one, "translationX", -screenWidth,0);
        animatwo = ObjectAnimator.ofFloat(tv_two, "translationX", screenWidth,0);
        animathree = ObjectAnimator.ofFloat(tv_three, "translationX",-screenWidth,0);
        animafour = ObjectAnimator.ofFloat(tv_four, "translationX",screenWidth,0);
        animaloading = ObjectAnimator.ofFloat(iv_loading, "rotation",0,359);
        animaloading.setRepeatCount(Integer.MAX_VALUE);

        animaOne.addListener(new AnimationL(tv_one));
        animatwo.addListener(new AnimationL(tv_two));
        animathree.addListener(new AnimationL(tv_three));
        animafour.addListener(new AnimationL(tv_four));
        animaloading.addListener(new AnimationL(iv_loading));

        AnimatorSet set=new AnimatorSet();
        set.playSequentially(animaOne,animatwo,animathree,animafour,animaloading);
        set.setDuration(800);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void initView() {
        tv_one= (TextView) findViewById(R.id.tv_one);
        tv_two= (TextView) findViewById(R.id.tv_two);
        tv_three= (TextView) findViewById(R.id.tv_three);
        tv_four= (TextView) findViewById(R.id.tv_four);
        iv_loading= (ImageView) findViewById(R.id.iv_loading);
    }

    class AnimationL implements Animator.AnimatorListener{

        private View view;
        public AnimationL(View view){
            this.view=view;
        }
        @Override
        public void onAnimationStart(Animator animation) {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (view.getId()==R.id.tv_four){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                },3000);
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
