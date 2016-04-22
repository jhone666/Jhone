package com.jhone.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhone.demo.R;
import com.jhone.demo.barcode.CaptureActivity;
import com.jhone.demo.event.CodeEvent;
import com.jhone.demo.utils.FragmentUtils;
import com.jhone.demo.utils.ToastUtils;
import com.yolanda.nohttp.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jhone on 2016/4/20.
 */
public class FoundFragment extends Fragment{

    private View view;
    @Bind(R.id.tv_scan)TextView tv_scan;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view=inflater.inflate(R.layout.fragment_found,null);
        FragmentUtils.initFragment(view);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @OnClick(R.id.tv_scan)
    public void toScan(){
        startActivity(new Intent(getActivity(), CaptureActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(CodeEvent event){
        tv_scan.setText(event.getResult().getText());
        Logger.v(event.getResult().getText());
    }
}
