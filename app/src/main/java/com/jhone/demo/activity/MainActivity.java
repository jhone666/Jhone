package com.jhone.demo.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.jhone.demo.R;
import com.jhone.demo.common.AppApplication;
import com.jhone.demo.fragment.ConnectFragment;
import com.jhone.demo.fragment.FoundFragment;
import com.jhone.demo.fragment.HomePageFragment;
import com.jhone.demo.fragment.MapFragment;
import com.jhone.demo.utils.DeviceUtil;
import com.jhone.demo.utils.ToastUtils;
import com.jhone.demo.view.SlideMenu;
import com.jhone.demo.view.TabRadioButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnCheckedChangeListener{


    @Bind(R.id.radioGroup) RadioGroup radioGroup;
    @Bind(R.id.first_rb) TabRadioButton first_rb;
    @Bind(R.id.second_rb) TabRadioButton second_rb;
    @Bind(R.id.third_rb) TabRadioButton third_rb;
    @Bind(R.id.four_rb) TabRadioButton four_rb;
    @Bind(R.id.slideMenu) SlideMenu slideMenu;
    @Bind(R.id.menu) ImageView menu;
    @Bind(R.id.title) TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        radioGroup.setOnCheckedChangeListener(this);

        homepageFragment=new HomePageFragment();
        connectFragment=new ConnectFragment();
        mapFragment=new MapFragment();
        foundFragment=new FoundFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, homepageFragment).commit();
        mCurrentFragment=homepageFragment;
        first_rb.setChecked(true);

        setCurrentTitle("jhone");
        slideMenu.setShowAnim(true);

        DeviceUtil.setStatusBar(this);

    }

    @OnClick(R.id.menu)
    public void toggleMenu(){
        slideMenu.toggleMenu();
    }

    private Fragment homepageFragment,connectFragment,mapFragment,foundFragment;
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.first_rb :
                switchTab(homepageFragment);
                setCurrentTitle("jhone");
                break;
            case R.id.second_rb :
                switchTab(connectFragment);
                setCurrentTitle("联系人");
                break;
            case R.id.third_rb :
                switchTab(mapFragment);
                setCurrentTitle("地图");
                break;
            case R.id.four_rb :
                switchTab(foundFragment);
                setCurrentTitle("发现");
                break;
            default :
                break;
        }
    }

    private Fragment mCurrentFragment=homepageFragment;
    public void switchTab(Fragment desFragment) {
        if (mCurrentFragment != desFragment) {
            if (!desFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).add(R.id.content, desFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).show(desFragment).commit();
            }
            mCurrentFragment = desFragment;
        }
    }

    private long mCurrentBackTime; // 保存当前 按下返回键的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (slideMenu.isOpen()){
            slideMenu.closeMenu();
            return true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if ((System.currentTimeMillis() - mCurrentBackTime) > 2000) {
            ToastUtils.showShort("再按一次退出系统");
            mCurrentBackTime = currentTimeMillis;
            return false;
        } else {
            AppApplication.getInstance().exitApp();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setCurrentTitle(String str){
        this.title.setText(str);
    }
}
