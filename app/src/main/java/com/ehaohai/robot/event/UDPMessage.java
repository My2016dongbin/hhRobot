package com.ehaohai.robot.event;

public class UDPMessage {
    String message;

    public UDPMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
