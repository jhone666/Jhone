package com.jhone.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import com.jhone.demo.base.BaseActivity;


public class GestureBackUtil {

	private GestureDetector detector;
	private View ignoreView;

	public GestureBackUtil(final Context context) {

		detector = new GestureDetector(context, new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
									float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
								   float velocityX, float velocityY) {
				if (e1 == null || e2 == null) {
					return false;
				}
				float disxp = e1.getX() - e2.getX();
				float disyp = e1.getY() - e2.getY();

				if (disxp < 0 && Math.abs(disxp) > Math.abs(disyp)) {
					// 如果包含ViewPager的界面显示的是第一个界面，那么向右滑时返回
					if (ignoreView == null
							|| (ignoreView instanceof ViewPager && ((ViewPager) ignoreView)
									.getCurrentItem() == 0)) {
						// 向右滑
						if (context instanceof BaseActivity) {
							((BaseActivity) context).onBackPressed();
						} else {
							((Activity) context).finish();
						}
						return true;
					} else if (ignoreView instanceof Gallery) {
						if (!inRangeOfView(ignoreView, e1)) {
							
							if (context instanceof BaseActivity) {
								((BaseActivity) context).onBackPressed();
							} else {
								((Activity) context).finish();
							}
						}

						return false;
					}
				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (detector == null) {//不明原因的错误
			return false;
		}
		return detector.onTouchEvent(event);
	}

	/**
	 * 如果界面包含有多个pager, 一定要设置，否则无法回看界面
	 * 
	 * @param ignoreView
	 */
	public void setIgnoreViewPager(View ignoreView) {
		this.ignoreView = ignoreView;
	}

	public View getIgnoreViewPager() {
		return ignoreView;
	}

	public static boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
				|| ev.getY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}

}
