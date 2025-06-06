package com.ehaohai.robot.ui.multitype;

public class Audio {
    private String name;
    private String time;
    private String path;

    public Audio(String name, String time, String path) {
        this.name = name;
        this.time = time;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
