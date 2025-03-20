package com.ehaohai.robot.wifi;

import com.ehaohai.robot.utils.HhLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadcast {
    
    private DatagramSocket socket;
    private final int PORT = 9990;

    public UDPBroadcast() throws Exception {
        socket = new DatagramSocket();
    }

    public void sendBroadcast(String message) {
        HhLog.e("UDP 发送: " + message);
        new Thread(() -> {
            try{
                byte[] buffer = message.getBytes();
                InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, PORT);
                socket.send(packet);
            }catch (Exception e){
                HhLog.e("UDP " + e.toString());
            }
        }).start();
    }
}