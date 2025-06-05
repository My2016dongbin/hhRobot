package com.ehaohai.robot.model;

public class Heart {
    //设备编号
    private String DeviceSn;
    //环境温度
    private double ambTemperature;
    //环境湿度
    private double ambHumidity;
    //速度
    private double linearSpeed;
    //电量
    private int batteryPercentage;
    //烟雾浓度
    private int somkeValue;

    public Heart() {
    }

    public Heart(String deviceSn, double ambTemperature, double ambHumidity, double linearSpeed, int batteryPercentage, int somkeValue) {
        DeviceSn = deviceSn;
        this.ambTemperature = ambTemperature;
        this.ambHumidity = ambHumidity;
        this.linearSpeed = linearSpeed;
        this.batteryPercentage = batteryPercentage;
        this.somkeValue = somkeValue;
    }

    public String getDeviceSn() {
        return DeviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        DeviceSn = deviceSn;
    }

    public double getAmbTemperature() {
        return ambTemperature;
    }

    public void setAmbTemperature(double ambTemperature) {
        this.ambTemperature = ambTemperature;
    }

    public double getAmbHumidity() {
        return ambHumidity;
    }

    public void setAmbHumidity(double ambHumidity) {
        this.ambHumidity = ambHumidity;
    }

    public double getLinearSpeed() {
        return linearSpeed;
    }

    public void setLinearSpeed(double linearSpeed) {
        this.linearSpeed = linearSpeed;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public int getSomkeValue() {
        return somkeValue;
    }

    public void setSomkeValue(int somkeValue) {
        this.somkeValue = somkeValue;
    }

    @Override
    public String toString() {
        return "Heart{" +
                "DeviceSn='" + DeviceSn + '\'' +
                ", ambTemperature=" + ambTemperature +
                ", ambHumidity=" + ambHumidity +
                ", linearSpeed=" + linearSpeed +
                ", batteryPercentage=" + batteryPercentage +
                ", somkeValue=" + somkeValue +
                '}';
    }
}
