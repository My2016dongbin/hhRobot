package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityDeviceListBinding;
import com.ehaohai.robot.event.UDPMessage;
import com.ehaohai.robot.ui.viewmodel.DeviceListViewModel;
import com.ehaohai.robot.wifi.UDPReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

public class DeviceListActivity extends BaseLiveActivity<ActivityDeviceListBinding, DeviceListViewModel> {
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
    protected ActivityDeviceListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_device_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public DeviceListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(DeviceListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().device.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {
        binding.messageList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < obtainViewModel().deviceList.size();i++){
            View view = inflater.inflate(R.layout.item_device,null,false);
            TextView from = view.findViewById(R.id.from);
            from.setText(obtainViewModel().deviceList.get(i).getFrom());
            TextView name = view.findViewById(R.id.name);
            name.setText(obtainViewModel().deviceList.get(i).getName());
            TextView code = view.findViewById(R.id.code);
            code.setText(obtainViewModel().deviceList.get(i).getCode());
            View viewEmpty = view.findViewById(R.id.view);
            if(i == 0){
                viewEmpty.setVisibility(View.VISIBLE);
            }else{
                viewEmpty.setVisibility(View.GONE);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DeviceListActivity.this, MainActivity.class));
                }
            });
            binding.messageList.addView(view);
        }
        View add = inflater.inflate(R.layout.item_device_add,null,false);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceListActivity.this,DeviceSearchActivity.class));
            }
        });
        binding.messageList.addView(add);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}