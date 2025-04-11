package com.ehaohai.robot.ui.multitype;

public class Picture {
    private String id;
    private String name;
    private String url;
    private boolean showChoose;
    private boolean selected;

    public Picture(String id, String name, String url, boolean showChoose, boolean selected) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.showChoose = showChoose;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
