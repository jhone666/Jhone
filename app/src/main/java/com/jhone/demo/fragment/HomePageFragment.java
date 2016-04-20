package com.jhone.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jhone.demo.R;
import com.jhone.demo.utils.FragmentUtils;

/**
 * Created by jhone on 2016/4/20.
 */
public class HomePageFragment extends Fragment{

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_homepage,null);
        FragmentUtils.initFragment(view);
        return view;
    }
}
