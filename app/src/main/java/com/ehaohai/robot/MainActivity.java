package com.ehaohai.robot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityMainBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.ui.activity.ControlActivity;
import com.ehaohai.robot.ui.activity.DeviceSearchActivity;
import com.ehaohai.robot.ui.activity.MineActivity;
import com.ehaohai.robot.ui.viewmodel.MainViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseLiveActivity<ActivityMainBinding, MainViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    ///退出登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Exit event) {
        finish();
    }

    private void init_() {
        EventBus.getDefault().register(this);
    }

    private void bind_() {
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ControlActivity.class));
            }
        });
        binding.mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MineActivity.class));
            }
        });
        binding.device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DeviceSearchActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected ActivityMainBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MainViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MainViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}