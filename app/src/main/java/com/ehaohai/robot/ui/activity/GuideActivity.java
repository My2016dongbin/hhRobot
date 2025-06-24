package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityGuideBinding;
import com.ehaohai.robot.ui.viewmodel.GuideViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class GuideActivity extends BaseLiveActivity<ActivityGuideBinding, GuideViewModel> {

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
        CommonUtil.click(binding.book, new Action() {
            @Override
            public void click() {
                Toast.makeText(GuideActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        CommonUtil.click(binding.video, new Action() {
            @Override
            public void click() {
                Toast.makeText(GuideActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        CommonUtil.click(binding.service, new Action() {
            @Override
            public void click() {
                Toast.makeText(GuideActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        CommonUtil.click(binding.suggest, new Action() {
            @Override
            public void click() {
                Toast.makeText(GuideActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected ActivityGuideBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_guide);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public GuideViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(GuideViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}