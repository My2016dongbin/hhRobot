package com.ehaohai.robot.constant;

import com.ehaohai.robot.utils.CommonData;

public class URLConstant {

    public static final String BASE_PATH = "http://172.16.10.54:8444/";//Release-穿透 //平台8081
    public static String LOCAL_IP = "172.16.10.54";//测试本地离线模式 172.16.10.162
    public static String LOCAL_PATH = "http://172.16.10.54:8001/";//测试本地离线模式

    public static void setLocalPath(String ip){
        LOCAL_IP = ip;
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

    //上传音频-post
    public static String UPLOAD_AUDIO(){
        return LOCAL_PATH+"robot/v1/app/upload_video";
    }
    //删除音频-post
    public static String DELETE_AUDIO(){
        return LOCAL_PATH+"robot/v1/app/delete_video";
    }
    //重命名音频-post
    public static String RENAME_AUDIO(){
        return LOCAL_PATH+"robot/v1/app/rename_video";
    }
    //获取音频列表-post
    public static String GET_AUDIO_LIST(){
        return LOCAL_PATH+"robot/v1/app/search_video";
    }
    //播放音频-post
    public static String PLAY_AUDIO(){
        return LOCAL_PATH+"robot/v1/app/play_video";
    }

    //新增人脸文件夹POST
    public static String ADD_FACE_FILE(){
        return LOCAL_PATH+"robot/v1/faces/add_face";
    }
    //获取人脸文件夹列表POST
    public static String GET_FACE_FILE_LIST(){
        return LOCAL_PATH+"robot/v1/faces/find";
    }
    //人脸照片列表GET
    public static String GET_FACE_LIST(){
        return LOCAL_PATH+"robot/v1/faces";
    }
    //删除人脸图片DELETE /delete_face/{face_uuid}/{img_name}
    public static String DELETE_FACE_PICTURE(){
        return LOCAL_PATH+"robot/v1/faces/delete_face";
    }
    //删除文件夹DELETE /delete_folder/{face_uuid}
    public static String DELETE_FACE_FILE(){
        return LOCAL_PATH+"robot/v1/faces/delete_folder";
    }


    //任务相关
    public static String TASK_COMMAND(){
        //return LOCAL_PATH+"robot/v1/task";
        return LOCAL_PATH+"robot/v1/app/control";
    }

}
