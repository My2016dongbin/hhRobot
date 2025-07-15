package com.ehaohai.robot.ui.multitype;

import java.util.List;

public class Task {
    private String task_id;
    private String task_name;
    private int task_timer;
    private List<Route> task_route;
    private String start_time;
    private String taskStatus;

    public Task() {
    }

    public Task(String task_id, String task_name, int task_timer, List<Route> task_route, String taskStatus, String start_time) {
        this.task_id = task_id;
        this.task_name = task_name;
        this.task_timer = task_timer;
        this.task_route = task_route;
        this.taskStatus = taskStatus;
        this.start_time = start_time;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getTask_timer() {
        return task_timer;
    }

    public void setTask_timer(int task_timer) {
        this.task_timer = task_timer;
    }

    public List<Route> getTask_route() {
        return task_route;
    }

    public void setTask_route(List<Route> task_route) {
        this.task_route = task_route;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    /**
     * "point_index":1,#站点号，对应single_nav中的站点号
     * "poi_name":"point001",#站点名称，手动设定，不需要管
     * "point_type":"normal", #站点类型：normal,普通点，到达后无需处理，charge，充点电，lift_out,电梯外部点，lift_inside，电梯内部点
     * "lift_param":1,#到达电梯点后，呼叫电梯到几楼，仅针对电梯点（lift_out,lift_inside）有效
     */
    public static class Route{
        private int point_index;
        private String poi_name;
        private String point_type;
        private int lift_param;
        private String x;
        private String y;
        private String z;

        public Route() {
        }

        public Route(int point_index, String poi_name, String point_type, int lift_param) {
            this.point_index = point_index;
            this.poi_name = poi_name;
            this.point_type = point_type;
            this.lift_param = lift_param;
        }

        public Route(int point_index, String poi_name, String point_type, int lift_param, String x, String y, String z) {
            this.point_index = point_index;
            this.poi_name = poi_name;
            this.point_type = point_type;
            this.lift_param = lift_param;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getZ() {
            return z;
        }

        public void setZ(String z) {
            this.z = z;
        }

        public int getPoint_index() {
            return point_index;
        }

        public void setPoint_index(int point_index) {
            this.point_index = point_index;
        }

        public String getPoi_name() {
            return poi_name;
        }

        public void setPoi_name(String poi_name) {
            this.poi_name = poi_name;
        }

        public String getPoint_type() {
            return point_type;
        }

        public void setPoint_type(String point_type) {
            this.point_type = point_type;
        }

        public int getLift_param() {
            return lift_param;
        }

        public void setLift_param(int lift_param) {
            this.lift_param = lift_param;
        }

        @Override
        public String toString() {
            return "Route{" +
                    "point_index=" + point_index +
                    ", poi_name='" + poi_name + '\'' +
                    ", point_type='" + point_type + '\'' +
                    ", lift_param=" + lift_param +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "task_id='" + task_id + '\'' +
                ", task_name='" + task_name + '\'' +
                ", task_timer=" + task_timer +
                ", task_route=" + task_route +
                ", start_time='" + start_time + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
