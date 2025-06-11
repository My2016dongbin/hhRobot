package com.ehaohai.robot.utils;


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


    public static boolean networkMode = false;//在线模式&&离线模式

    public static long loginDownLong = 0;//登出计时器

    public static String dogUrl = "rtsp://172.16.10.162:554/live.stream";
    public static String lightUrl = "rtsp://172.16.10.162:554/visible";
    public static String hotUrl = "rtsp://172.16.10.162:554/thermal";
    public static String sn = "";


    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
    }
}
