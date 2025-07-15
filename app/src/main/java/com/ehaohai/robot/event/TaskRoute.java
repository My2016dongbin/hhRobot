package com.ehaohai.robot.event;

import com.ehaohai.robot.ui.multitype.Point;

import java.util.List;

public class TaskRoute {
    private List<Point> route;

    public TaskRoute() {
    }

    public TaskRoute(List<Point> route) {
        this.route = route;
    }

    public List<Point> getRoute() {
        return route;
    }

    public void setRoute(List<Point> route) {
        this.route = route;
    }
}
