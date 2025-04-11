package com.ehaohai.robot.ui.activity;

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
import com.ehaohai.robot.databinding.ActivityMineBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.ui.viewmodel.MineViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

public class MineActivity extends BaseLiveActivity<ActivityMineBinding, MineViewModel> {

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
        CommonUtil.click(binding.exit, new Action() {
            @Override
            public void click() {
                CommonUtil.showConfirm(MineActivity.this, "确认退出当前账户吗？", "退出", "取消", new Action() {
                    @Override
                    public void click() {
                        obtainViewModel().loginOut();
                        startActivity(new Intent(MineActivity.this, LoginActivity.class));
                        EventBus.getDefault().post(new Exit());
                        finish();
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },true);
            }
        });
    }

    @Override
    protected ActivityMineBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_mine);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MineViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MineViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}