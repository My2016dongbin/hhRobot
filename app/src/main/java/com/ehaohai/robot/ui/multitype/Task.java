package com.ehaohai.robot.ui.multitype;

public class Task {
    private String id;
    private String taskName;
    private String taskCircle;
    private String taskRoute;
    private String taskStatus;
    private String taskStartTime;

    public Task() {
    }

    public Task(String id, String taskName, String taskCircle, String taskRoute, String taskStatus, String taskStartTime) {
        this.id = id;
        this.taskName = taskName;
        this.taskCircle = taskCircle;
        this.taskRoute = taskRoute;
        this.taskStatus = taskStatus;
        this.taskStartTime = taskStartTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskCircle() {
        return taskCircle;
    }

    public void setTaskCircle(String taskCircle) {
        this.taskCircle = taskCircle;
    }

    public String getTaskRoute() {
        return taskRoute;
    }

    public void setTaskRoute(String taskRoute) {
        this.taskRoute = taskRoute;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskCircle='" + taskCircle + '\'' +
                ", taskRoute='" + taskRoute + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", taskStartTime='" + taskStartTime + '\'' +
                '}';
    }
}
