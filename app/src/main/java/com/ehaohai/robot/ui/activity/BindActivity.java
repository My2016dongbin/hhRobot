package com.ehaohai.robot.ui.activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityBindBinding;
import com.ehaohai.robot.event.DeviceRefresh;
import com.ehaohai.robot.ui.viewmodel.BindViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

public class BindActivity extends BaseLiveActivity<ActivityBindBinding, BindViewModel> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {
        obtainViewModel().getDeviceInfo();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        ///取消
        CommonUtil.click(binding.dismiss, new Action() {
            @Override
            public void click() {
                Toast.makeText(BindActivity.this, "已取消绑定", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        ///确定
        CommonUtil.click(binding.confirm, new Action() {
            @Override
            public void click() {
                EventBus.getDefault().post(new DeviceRefresh());
                Toast.makeText(BindActivity.this, "设备绑定成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected ActivityBindBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_bind);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public BindViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(BindViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().message.observe(this, this::messageChanged);
    }

    private void messageChanged(String message) {
        binding.textFrom.setText("绑定机器狗Go2 EDU");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}