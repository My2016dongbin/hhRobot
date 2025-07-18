package com.ehaohai.robot.ui.multitype;

import java.util.List;

public class Face {
    private String id;
    private String faceName;
    private List<Pic> list;

    private boolean showChoose;
    private boolean selected;

    public Face() {
    }

    public Face(String id, String faceName, List<Pic> list, boolean showChoose, boolean selected) {
        this.id = id;
        this.faceName = faceName;
        this.list = list;
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

    public List<Pic> getList() {
        return list;
    }

    public void setList(List<Pic> list) {
        this.list = list;
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

    public static class Pic{
        private String faceName;
        private String imgName;
        private String imgUrl;

        public Pic() {
        }

        public Pic(String faceName, String imgName, String imgUrl) {
            this.faceName = faceName;
            this.imgName = imgName;
            this.imgUrl = imgUrl;
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

        @Override
        public String toString() {
            return "Pic{" +
                    "faceName='" + faceName + '\'' +
                    ", imgName='" + imgName + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "Face{" +
                "id='" + id + '\'' +
                ", faceName='" + faceName + '\'' +
                ", list=" + list +
                ", showChoose=" + showChoose +
                ", selected=" + selected +
                '}';
    }
}
