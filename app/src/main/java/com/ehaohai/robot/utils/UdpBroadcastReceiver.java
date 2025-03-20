package com.ehaohai.robot.utils;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpBroadcastReceiver extends Thread {
    private static final int BROADCAST_PORT = 9990; // 设备广播的端口
    private boolean running = true;

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("172.16.50.82"));
            socket.setBroadcast(true);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            HhLog.e("UDPReceiver", "开始监听 UDP 广播...");

            while (running) {
                socket.receive(packet); // 阻塞等待 UDP 数据

                // 解析收到的 JSON 数据
                String receivedData = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                HhLog.e("UDPReceiver", "收到广播消息: " + receivedData);
            }

            socket.close();
        } catch (Exception e) {
            HhLog.e("UDPReceiver", "UDP 监听异常: " + e.getMessage());
        }
    }

    public void stopReceiver() {
        running = false;
    }
}