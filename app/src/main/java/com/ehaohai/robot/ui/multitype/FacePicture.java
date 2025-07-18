package com.ehaohai.robot.ui.multitype;

public class FacePicture {
    private String id;
    private String faceName;
    private String imgName;
    private String imgUrl;
    private String timeStamp;
    private String uuid;

    private boolean showChoose;
    private boolean selected;

    public FacePicture() {
    }

    public FacePicture(String id, String faceName, String imgName, String imgUrl, String timeStamp, String uuid, boolean showChoose, boolean selected) {
        this.id = id;
        this.faceName = faceName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.timeStamp = timeStamp;
        this.uuid = uuid;
        this.showChoose = showChoose;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isShowChoose() {
        return showChoose;
    }

    public void setShowChoose(boolean showChoose) {
        this.showChoose = showChoose;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "FacePicture{" +
                "id='" + id + '\'' +
                ", faceName='" + faceName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", uuid='" + uuid + '\'' +
                ", showChoose=" + showChoose +
                ", selected=" + selected +
                '}';
    }
}
