package com.ehaohai.robot.ui.activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityDeviceSettingBinding;
import com.ehaohai.robot.ui.viewmodel.DeviceSettingViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class DeviceSettingActivity extends BaseLiveActivity<ActivityDeviceSettingBinding, DeviceSettingViewModel> {
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
        binding.back.setOnClickListener(view -> {
            finish();
        });
        ///复制
        CommonUtil.click(binding.copy, new Action() {
            @Override
            public void click() {
                if(binding.name1Edit.getText().toString().isEmpty() || binding.name2Edit.getText().toString().isEmpty()){
                    Toast.makeText(DeviceSettingActivity.this, "请选择或输入机器狗信息", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        ///移除
        CommonUtil.click(binding.remove, new Action() {
            @Override
            public void click() {

            }
        });
    }

    @Override
    protected ActivityDeviceSettingBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_device_setting);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public DeviceSettingViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(DeviceSettingViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().message.observe(this, this::messageChanged);
    }

    private void messageChanged(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}