package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityForAppBinding;
import com.ehaohai.robot.ui.viewmodel.ForAppViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class ForAppActivity extends BaseLiveActivity<ActivityForAppBinding, ForAppViewModel> {

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
        CommonUtil.click(binding.list, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.points, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(ForAppActivity.this,PointManageActivity.class));
            }
        });
        CommonUtil.click(binding.guide, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(ForAppActivity.this,GuideActivity.class));
            }
        });
        CommonUtil.click(binding.update, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.one, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.two, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.three, new Action() {
            @Override
            public void click() {

            }
        });
    }

    @Override
    protected ActivityForAppBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_for_app);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ForAppViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ForAppViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}