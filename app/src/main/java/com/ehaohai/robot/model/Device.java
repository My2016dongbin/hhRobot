package com.ehaohai.robot.model;

public class Device {
    private String id;
    private String name;
    private String from;
    private String image;
    private String state;
    private String code;

    public Device(String id, String name, String from, String image, String state, String code) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.image = image;
        this.state = state;
        this.code = code;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
