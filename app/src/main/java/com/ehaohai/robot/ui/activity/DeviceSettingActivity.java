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
import com.ehaohai.robot.databinding.ActivityDeviceSettingBinding;
import com.ehaohai.robot.event.DeviceRefresh;
import com.ehaohai.robot.event.DeviceRemove;
import com.ehaohai.robot.ui.viewmodel.DeviceSettingViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

public class DeviceSettingActivity extends BaseLiveActivity<ActivityDeviceSettingBinding, DeviceSettingViewModel> {
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
        ///复制
        CommonUtil.click(binding.copy, new Action() {
            @Override
            public void click() {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.snEdit.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(DeviceSettingActivity.this, "设备SN已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
        ///移除
        CommonUtil.click(binding.remove, new Action() {
            @Override
            public void click() {
                EventBus.getDefault().post(new DeviceRemove());
                EventBus.getDefault().post(new DeviceRefresh());
                Toast.makeText(DeviceSettingActivity.this, "设备移除成功", Toast.LENGTH_SHORT).show();
                finish();
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
        binding.snEdit.setText("S21873DHSBUSU23726");
        binding.typeEdit.setText("Go2 EDU");
        binding.nameEdit.setText("浩海机器狗");
        binding.textFrom.setText("来自添加设备");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}