package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityDeviceSearchBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.event.UDPMessage;
import com.ehaohai.robot.model.UdpMessage;
import com.ehaohai.robot.ui.viewmodel.DeviceSearchViewModel;
import com.ehaohai.robot.utils.Common;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.wifi.UDPBroadcast;
import com.ehaohai.robot.wifi.UDPReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

public class DeviceSearchActivity extends BaseLiveActivity<ActivityDeviceSearchBinding, DeviceSearchViewModel> {
    UDPReceiver udpReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        fullScreen(this);
        init_();
        bind_();
    }

    ///UDP消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(UDPMessage event) {
        String message = event.getMessage();
        try {
            JSONObject object = new JSONObject(message);
            String msgType = object.getString("msgType");
            String deviceSn = object.getString("DeviceSn");
            String timestamp = object.getString("timestamp");
            String IP = object.getString("IP");

            for (int i = 0; i < obtainViewModel().messageList.size(); i++) {
                UdpMessage udpMessage = obtainViewModel().messageList.get(i);
                if(Objects.equals(udpMessage.getDeviceSn(), deviceSn)){
                    obtainViewModel().messageList.set(i,new UdpMessage(msgType,deviceSn,timestamp,IP));
                    obtainViewModel().message.postValue(event.getMessage());
                    return;
                }
                if(i == obtainViewModel().messageList.size()-1){
                    obtainViewModel().messageList.add(new UdpMessage(msgType,deviceSn,timestamp,IP));
                    obtainViewModel().message.postValue(event.getMessage());
                }
            }
            if(obtainViewModel().messageList.isEmpty()){
                obtainViewModel().messageList.add(new UdpMessage(msgType,deviceSn,timestamp,IP));
                obtainViewModel().message.postValue(event.getMessage());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void init_() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 启动接收线程
        udpReceiver = new UDPReceiver(wifiManager);
        new Thread(udpReceiver).start();


        ///TODO 暂时绕过校验
        binding.name1Edit.setText("ehaohai");
        binding.name2Edit.setText("ehaohai");
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        ///绑定
        binding.bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.name1Edit.getText().toString().isEmpty() || binding.name2Edit.getText().toString().isEmpty()){
                    Toast.makeText(DeviceSearchActivity.this, "请选择或输入机器狗信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                CommonData.offlineModeSN = binding.name1Edit.getText().toString();
                CommonData.offlineModeIP = binding.name2Edit.getText().toString();

                if(CommonData.networkMode){
                    //在线模式
                    startActivity(new Intent(DeviceSearchActivity.this,BindActivity.class));
                    finish();
                }else{
                    //离线模式
                    startActivity(new Intent(DeviceSearchActivity.this,OfflineLoginActivity.class));
                    finish();
                }
            }
        });
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

        obtainViewModel().message.observe(this, this::messageChanged);
    }

    private void messageChanged(String message) {
        binding.messageList.removeAllViews();
        for (int i = 0; i < obtainViewModel().messageList.size();i++){
            UdpMessage udpMessage = obtainViewModel().messageList.get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.item_message,null,false);
            TextView sn = view.findViewById(R.id.sn);
            TextView ip = view.findViewById(R.id.ip);
            sn.setText(udpMessage.getDeviceSn());
            ip.setText(udpMessage.getIP());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.name1Edit.setText(udpMessage.getDeviceSn());
                    binding.name2Edit.setText(udpMessage.getIP());
                }
            });
            binding.messageList.addView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        udpReceiver.stop();
    }
}