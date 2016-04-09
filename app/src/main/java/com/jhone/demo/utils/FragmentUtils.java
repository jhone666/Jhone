package com.jhone.demo.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jhone on 2016/3/23.
 */
public class FragmentUtils {

    /**
     * Fragment 初始方法，以免切换回来时没有了内容
     *
     * @param view 当前Fragment中的View视图
     */
    public static void initFragment(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

}
