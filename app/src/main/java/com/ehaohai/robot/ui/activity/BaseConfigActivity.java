package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityAccountSafeBinding;
import com.ehaohai.robot.databinding.ActivityBaseConfigBinding;
import com.ehaohai.robot.ui.viewmodel.AccountSafeViewModel;
import com.ehaohai.robot.ui.viewmodel.BaseConfigViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class BaseConfigActivity extends BaseLiveActivity<ActivityBaseConfigBinding, BaseConfigViewModel> {

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
        CommonUtil.click(binding.phone, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(BaseConfigActivity.this,PhoneSettingActivity.class));
            }
        });
        CommonUtil.click(binding.message, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(BaseConfigActivity.this,MessageSettingActivity.class));
            }
        });
        CommonUtil.click(binding.warn, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.clear, new Action() {
            @Override
            public void click() {

            }
        });
    }

    @Override
    protected ActivityBaseConfigBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_base_config);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public BaseConfigViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(BaseConfigViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}