package com.ehaohai.robot.model;

public class UdpMessage {
    private String msgType;
    private String DeviceSn;
    private String timestamp;
    private String IP;

    public UdpMessage(String msgType, String deviceSn, String timestamp, String IP) {
        this.msgType = msgType;
        DeviceSn = deviceSn;
        this.timestamp = timestamp;
        this.IP = IP;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getDeviceSn() {
        return DeviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        DeviceSn = deviceSn;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public String toString() {
        return "UdpMessage{" +
                "msgType='" + msgType + '\'' +
                ", DeviceSn='" + DeviceSn + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", IP='" + IP + '\'' +
                '}';
    }
}
