package com.ehaohai.robot.ui.multitype;

public class Audio {
    private String filename;
    private String filepath;
    private String modified_time;
    private String size;

    public Audio() {
    }

    public Audio(String filename, String filepath, String modified_time, String size) {
        this.filename = filename;
        this.filepath = filepath;
        this.modified_time = modified_time;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getModified_time() {
        return modified_time;
    }

    public void setModified_time(String modified_time) {
        this.modified_time = modified_time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
