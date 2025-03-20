package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityDeviceSearchBinding;
import com.ehaohai.robot.ui.viewmodel.DeviceSearchViewModel;
import com.ehaohai.robot.wifi.UDPBroadcast;
import com.ehaohai.robot.wifi.UDPReceiver;

public class DeviceSearchActivity extends BaseLiveActivity<ActivityDeviceSearchBinding, DeviceSearchViewModel> {
    UDPReceiver udpReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        Glide.with(this).load(getResources().getDrawable(R.drawable.dog))
                .transform(new GranularRoundedCorners(10,0,0,10))
                .into(binding.imageLeft);


        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 启动接收线程
        udpReceiver = new UDPReceiver(wifiManager);
        new Thread(udpReceiver).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 发送广播消息
                try {
                    UDPBroadcast broadcast = new UDPBroadcast();
                    broadcast.sendBroadcast("TurnOnLights");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },5000);
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
    }

    @Override
    protected ActivityDeviceSearchBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_device_search);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public DeviceSearchViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(DeviceSearchViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {
        binding.messageList.removeAllViews();
        for (int i = 0; i < obtainViewModel().stringList.size();i++){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.item_message,null,false);
            TextView text = view.findViewById(R.id.text);
            text.setText(obtainViewModel().stringList.get(i));
            binding.messageList.addView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        obtainViewModel().onDestroy();
        udpReceiver.stop();
    }
}