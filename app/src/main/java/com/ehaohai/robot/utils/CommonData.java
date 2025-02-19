package com.ehaohai.robot.utils;


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
    public static boolean hasSensor = false;
    public static boolean hasSign = false;
    public static boolean hasGet = false;
    public static int walkDistance = 0;
    public static int dis_int = 0;
    public static String warnType = "";
    public static boolean isUpdate = false;

    public static boolean hasMainApp = true;
    public static boolean hasMainVideo = true;
    public static boolean hasMainMessage = true;
    public static boolean hasMainMap = true;
    public static boolean hasMainMy = true;

    public static int versionCode = 0;
    public static int versionCodeService = 0;

    public static int videoAddingIndex = 0;
    public static int videoDeleteIndex = 0;
    public static String videoDeleteMonitorId = "";
    public static String videoDeleteChannelId = "";
    public static List<Integer> videoPlayingIndexList = new ArrayList<>();

    public static double lngAdding = 0;
    public static double latAdding = 0;
    public static long longAdding = 0;


    public static int mainTabIndex = 0;
    public static String search;

    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
        hasSensor = false;
    }
}
