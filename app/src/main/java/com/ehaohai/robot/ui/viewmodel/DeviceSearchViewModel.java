package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.model.UdpMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeviceSearchViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> message = new MutableLiveData<>();

    public List<String> stringList = new ArrayList<>();
    public List<UdpMessage> messageList = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

}
