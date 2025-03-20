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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeviceSearchViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    private static final int UDP_PORT = 9990;
    private boolean isListening = true;
    private Thread udpThread;
    public List<String> stringList = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    public void initWifiReceiver() {
        // 允许 UDP 多播
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("udpLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
        // 启动 UDP 监听线程
        startUdpListener();

        runHandler();
    }

    void runHandler(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String jsonStr = "runHandler {'msgType': 'discoverMsg', 'DeviceSn': '23423-03fd', 'timestamp': '20250310150623', 'IP': '172.16.50.82'}";
                Date date = new Date();
                stringList.add(jsonStr+date.toString());
                name.postValue(jsonStr+date.toString());
                runHandler();
            }
        },5000);
    }

    private void startUdpListener() {
        udpThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(UDP_PORT);
                socket.setBroadcast(true);

                while (isListening) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // 阻塞等待 UDP 数据

                    String receivedData = new String(packet.getData(), 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress();
                    Log.e("UDP_RECEIVE", "收到来自 " + senderAddress.getHostAddress() + " 的数据: " + receivedData);
                    Toast.makeText(context, "UDP_RECEIVE", Toast.LENGTH_SHORT).show();

                    // 解析 JSON 数据
                    parseJsonData(receivedData);
                }

                socket.close();
            } catch (Exception e) {
                Log.e("UDP_ERROR", "UDP 监听失败: " + e.getMessage());
            }
        });
        udpThread.start();
    }

    private void parseJsonData(String jsonStr) {
        Date date = new Date();
        stringList.add(jsonStr+date.toString());
        name.postValue(jsonStr+date.toString());
    }

    public void onDestroy() {
        isListening = false;
        if (udpThread != null && udpThread.isAlive()) {
            udpThread.interrupt();
        }
    }
}
