package com.ehaohai.robot.model;

import org.json.JSONObject;

public class Device {
    private String sn;
    private String name;
    private String ip;
    private JSONObject config;

    public Device() {
    }

    public Device(String sn, String name, String ip, JSONObject config) {
        this.sn = sn;
        this.name = name;
        this.ip = ip;
        this.config = config;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }
}
