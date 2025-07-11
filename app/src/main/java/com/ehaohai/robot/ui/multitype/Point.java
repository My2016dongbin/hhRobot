package com.ehaohai.robot.ui.multitype;

public class Point {
    private String id;
    private String name;
    private String path;
    private boolean showChoose;
    private boolean selected;

    public Point(String id, String name, String path, boolean showChoose, boolean selected) {
        this.id = id;
        this.name = name;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
