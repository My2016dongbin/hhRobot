package com.ehaohai.robot.ui.multitype;

import java.util.List;

public class Warn {
    private int count;
    private String id;
    private String timeStamp;
    private String deviceType;
    private String deviceName;
    private MoreInfo moreInfo;
    private String imgPath;
    private String isRead;

    private String msgType;
    private String deviceSn;
    private String detectType;

    private List<Err> errList;
    private String created_time;

    public List<Err> getErrList() {
        return errList;
    }

    public void setErrList(List<Err> errList) {
        this.errList = errList;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public static class Err{
        private String description;
        private int mask;
        private String timeStamp;

        public Err() {
        }

        public Err(String description, int mask, String timeStamp) {
            this.description = description;
            this.mask = mask;
            this.timeStamp = timeStamp;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getMask() {
            return mask;
        }

        public void setMask(int mask) {
            this.mask = mask;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
    public static class MoreInfo{
        private String name;
        private String alarmInfo;

        public MoreInfo() {
        }

        public MoreInfo(String name, String alarmInfo) {
            this.name = name;
            this.alarmInfo = alarmInfo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlarmInfo() {
            return alarmInfo;
        }

        public void setAlarmInfo(String alarmInfo) {
            this.alarmInfo = alarmInfo;
        }

        @Override
        public String toString() {
            return "MoreInfo{" +
                    "name='" + name + '\'' +
                    ", alarmInfo='" + alarmInfo + '\'' +
                    '}';
        }
    }

    public Warn() {
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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

    public MoreInfo getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(MoreInfo moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDetectType() {
        return detectType;
    }

    public void setDetectType(String detectType) {
        this.detectType = detectType;
    }

    @Override
    public String toString() {
        return "Warn{" +
                "count=" + count +
                ", id='" + id + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", moreInfo=" + moreInfo +
                ", imgPath='" + imgPath + '\'' +
                ", isRead='" + isRead + '\'' +
                ", msgType='" + msgType + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", detectType='" + detectType + '\'' +
                '}';
    }
}
