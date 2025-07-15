package com.ehaohai.robot.ui.multitype;

public class Point {
    //站点序号
    protected String id;
    //x坐标
    protected String x;
    //y坐标
    protected String y;
    //z坐标
    protected String z;
    //yaw方向
    protected String yaw;
    //保留位
    protected String a;
    //保留位
    protected String b;
    //保留位
    protected String c;
    //保留位
    protected String d;
    //楼层数
    protected String floor;
    //名称
    protected String name;

    ///自定义
    //序号
    protected int index;
    //点类型 #站点类型：normal,普通点，到达后无需处理，charge，充点电，lift_out,电梯外部点，lift_inside，电梯内部点
    protected String type = "normal";
    //任务到达楼层
    protected int taskFloor = 1;

    public Point() {
    }

    public Point(String id, String x, String y, String z, String yaw, String a, String b, String c, String d, String floor, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.floor = floor;
        this.name = name;
    }

    public int getTaskFloor() {
        return taskFloor;
    }

    public void setTaskFloor(int taskFloor) {
        this.taskFloor = taskFloor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getYaw() {
        return yaw;
    }

    public void setYaw(String yaw) {
        this.yaw = yaw;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                ", yaw='" + yaw + '\'' +
                ", a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d='" + d + '\'' +
                ", floor='" + floor + '\'' +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", type='" + type + '\'' +
                ", taskFloor=" + taskFloor +
                '}';
    }
}
