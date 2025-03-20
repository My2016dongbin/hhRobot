package com.ehaohai.robot.wifi;

import android.net.wifi.WifiManager;

import com.ehaohai.robot.utils.HhLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPReceiver implements Runnable {
    
    private final int PORT = 9990;
    private WifiManager.MulticastLock multicastLock;

    public UDPReceiver(WifiManager wifiManager) {
        // 申请 WiFi 多播锁，确保可以接收 UDP 广播
        this.multicastLock = wifiManager.createMulticastLock("UDPReceiver");
        this.multicastLock.setReferenceCounted(true);
        this.multicastLock.acquire();
    }
    public void run() {
        HhLog.e("UDP 启动: ");
        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress("0.0.0.0", 9990));
            socket.setBroadcast(true);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderIp = packet.getAddress();
                int senderPort = packet.getPort();
                HhLog.e( "📩 收到 UDP 消息：" + message + " 来自 " + senderIp + ":" + senderPort);
                processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HhLog.e("UDP 接收到消息: error" + e.toString());
        }
    }

    private void processMessage(String message) {
        // 处理接收到的消息，例如开关设备
        HhLog.e("UDP 接收到消息: " + message);
    }

    public void stop() {
        // 释放 WiFi 多播锁
        if (multicastLock != null) {
            multicastLock.release();
        }
    }
}