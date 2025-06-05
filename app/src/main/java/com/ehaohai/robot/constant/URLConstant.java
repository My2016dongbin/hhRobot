package com.ehaohai.robot.constant;

import com.ehaohai.robot.utils.CommonData;

public class URLConstant {

    public static final String BASE_PATH = "http://192.168.1.88:8444/";//Release-穿透 //平台8081
    public static String LOCAL_IP = "172.16.50.185";//测试本地离线模式
    public static String LOCAL_PATH = "http://172.16.50.185:8001/";//测试本地离线模式

    public static void setLocalPath(String ip){
        LOCAL_PATH = "http://" + ip + ":8001/";
    }


    public static final String PERMISSION_MAIN = BASE_PATH + "auth/api/auth/auth/user/auth";
    public static final String PERMISSION_PER = BASE_PATH + "auth/api/auth/auth/list/element/from/menu";

    //登录相关
    public static final String GET_LOGIN = "robot/v1/auth/login";//登录-
    public static final String GET_USER_INFO = BASE_PATH + "auth/api/auth/user/get/userinfo";//个人信息

    //离线登录-
    public static String OFFLINE_LOGIN(){
        return LOCAL_PATH+"robot/v1/auth/login";
    }
    //离线登出-
    public static String OFFLINE_LOGIN_OUT(){
        return LOCAL_PATH+"robot/v1/auth/logout";
    }
    //离线控制-
    public static String OFFLINE_CONTROL(){
        return LOCAL_PATH+"robot/v1/app/control";
    }
    //离线语音文件上传-
    public static String OFFLINE_FILE(){
        return LOCAL_PATH+"robot/v1/app/upload_video";
    }
    //离线语音播放-
    public static String OFFLINE_SPEAK(){
        return LOCAL_PATH+"robot/v1/app/play_video";
    }


    //报警列表-
    public static String ALARM_LIST(){
        return LOCAL_PATH+"robot/v1/alarm";
    }

    //获取日报警数-
    public static String ALARM_DAY_COUNT(){
        return LOCAL_PATH+"robot/v1/alarm/alarm_count";
    }

    //获取总报警数-
    public static String ALARM_ALL_COUNT(){
        return LOCAL_PATH+"robot/v1/alarm/alarm_all";
    }

    //心跳-
    public static String HEART(){
        return LOCAL_PATH+"robot/v1/machine/app/heartbeats";
    }

}
