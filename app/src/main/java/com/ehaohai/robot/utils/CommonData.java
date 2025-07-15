package com.ehaohai.robot.utils;


import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.ui.multitype.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qc
 * on 2023/7/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonData {
    public static String pushFlag = "robot";//对应平台配置项目标签 http://192.168.1.88:8082/#/resource/appVersion

    public static String token = "";
    public static double lat = 0;
    public static double lng = 0;
    public static double lat_old = 0;
    public static double lng_old = 0;
    public static String mode = "常规模式";
    public static List<Point> routeList = new ArrayList<>();


    public static boolean networkMode = false;//在线模式&&离线模式

    public static long loginDownLong = 0;//登出计时器

    public static String sn = "";
    public static String taskId = "";
    public static String taskStatus = "";


    public static String dogUrl(){
        return "rtsp://" + URLConstant.LOCAL_IP + ":554/live.stream";
    }
    public static String lightUrl(){
        return "rtsp://" + URLConstant.LOCAL_IP + ":554/visible";
    }
    public static String hotUrl(){
        return "rtsp://" + URLConstant.LOCAL_IP + ":554/thermal";
    }


    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
    }
}
