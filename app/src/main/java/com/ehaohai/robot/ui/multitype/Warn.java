package com.ehaohai.robot.ui.multitype;

public class Warn {
    private int count;
    private String id;
    private String findTime;
    private String deviceType;
    private String deviceName;
    private String findType;
    private String findResult;
    private String findImage;
    private boolean unRead;

    public Warn(int count, String id, String findTime, String deviceType, String deviceName, String findType, String findResult, String findImage, boolean unRead) {
        this.count = count;
        this.id = id;
        this.findTime = findTime;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.findType = findType;
        this.findResult = findResult;
        this.findImage = findImage;
        this.unRead = unRead;
    }

    public boolean isUnRead() {
        return unRead;
    }

    public void setUnRead(boolean unRead) {
        this.unRead = unRead;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getFindType() {
        return findType;
    }

    public void setFindType(String findType) {
        this.findType = findType;
    }

    public String getFindResult() {
        return findResult;
    }

    public void setFindResult(String findResult) {
        this.findResult = findResult;
    }

    public String getFindImage() {
        return findImage;
    }

    public void setFindImage(String findImage) {
        this.findImage = findImage;
    }
}
