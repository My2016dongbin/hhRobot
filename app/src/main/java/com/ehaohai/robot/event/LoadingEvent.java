package com.ehaohai.robot.event;

public class LoadingEvent {
    private boolean isShow;
    private String hint;

    public LoadingEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public LoadingEvent(boolean isShow, String hint) {
        this.isShow = isShow;
        this.hint = hint;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}

