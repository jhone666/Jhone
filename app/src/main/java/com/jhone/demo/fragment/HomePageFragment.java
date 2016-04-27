package com.jhone.demo.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhone.demo.R;
import com.jhone.demo.utils.FragmentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jhone on 2016/4/20.
 */
public class HomePageFragment extends Fragment{

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_homepage,null);
        FragmentUtils.initFragment(view);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Bind(R.id.tv_scrollTo)TextView tv_scrollTo;
    @OnClick({R.id.tv_scrollTo,R.id.tv_scrollBy})
    public void testScroll(View view){
        if (view.getId()==R.id.tv_scrollTo){
        }else {
            tv_scrollTo.bringToFront();
        }
    }
}
