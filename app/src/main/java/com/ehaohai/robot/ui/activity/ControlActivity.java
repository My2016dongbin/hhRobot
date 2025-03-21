package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityControlBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.ui.viewmodel.ControlViewModel;

import org.greenrobot.eventbus.EventBus;

public class ControlActivity extends BaseLiveActivity<ActivityControlBinding, ControlViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {

    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
    }

    @Override
    protected ActivityControlBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_control);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ControlViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ControlViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}