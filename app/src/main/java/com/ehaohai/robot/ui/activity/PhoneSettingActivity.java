package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPhoneSettingBinding;
import com.ehaohai.robot.ui.viewmodel.PhoneSettingViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class PhoneSettingActivity extends BaseLiveActivity<ActivityPhoneSettingBinding, PhoneSettingViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {

    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.save, new Action() {
            @Override
            public void click() {

            }
        });
    }

    @Override
    protected ActivityPhoneSettingBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_phone_setting);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PhoneSettingViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PhoneSettingViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}