package com.ehaohai.robot.permission;

import android.content.Context;

import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;


/**
 * Created by qc
 * on 2024/1/8.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonPermission {
    public static final String MAIN_APP = "app-application";//主菜单-首页
    public static final String MAIN_VIDEO = "app-video";//主菜单-视频
    public static final String MAIN_MESSAGE = "app-message";//主菜单-消息
    public static final String MAIN_MAP = "app-map";//主菜单-地图
    public static final String MAIN_MY = "app-setting";//主菜单-我的


    public static final String APP_BANNER = "app-application-btn-banner";//首页-轮播图
    public static final String APP_ALARM = "app-application-btn-fireAlarm";//首页-报警管理
    public static final String APP_SATELLITE = "app-application-btn-satelliteFireAlarm";//首页-卫星遥感
    public static final String APP_DANGER = "app-application-btn-danger";//首页-隐患排查
    public static final String APP_UPLOAD = "app-application-btn-fire";//首页-火情上报
    public static final String APP_VIDEO = "app-application-btn-video";//首页-视频监控
    public static final String APP_TASK = "app-application-btn-task";//首页-任务单
    public static final String APP_RANGER = "app-application-btn-ranger";//首页-防火员
    public static final String APP_NEWS = "app-application-btn-news";//首页-要闻
    public static final String APP_SIGN = "app-application-btn-sign";//首页-考勤管理
    public static final String APP_WEATHER = "app-application-btn-weather";//首页-天气
    public static final String APP_ALARM_INFO = "app-application-btn-alarmInfo";//首页-报警信息

    public static final String VIDEO_CONTROL = "app-video-btn-control";//视频-控制
    public static final String VIDEO_STAR = "app-video-btn-star";//视频-收藏

    public static final String MESSAGE_CLEAR = "app-message-btn-clear";//消息-清除未读

    public static final String MAP_ALARM_LIST = "app-map-btn-alarmList";//地图-报警列表
    public static final String MAP_RESOURCE_LIST = "app-map-btn-resourceList";//地图-资源列表
    public static final String MAP_RESOURCE_ADD = "app-map-btn-resourceAdd";//地图-资源添加
    public static final String MAP_RESOURCE_SEARCH = "app-map-btn-resourceSearch";//地图-资源搜索
    public static final String MAP_SATELLITE_SETTING = "app-map-btn-satelliteSetting";//地图-卫星-设置
    public static final String MAP_SATELLITE_LIST = "app-map-btn-satelliteList";//地图-卫星-列表
    public static final String MAP_SATELLITE_SEARCH = "app-map-btn-satelliteSearch";//地图-卫星-查询
    public static final String MAP_Grid_COMMUNITY = "app-map-btn-gridCommunity";//地图-网格-社区
    public static final String MAP_TASK = "app-map-btn-task";//地图-任务
    public static final String MAP_FIRE_HANDLE = "app-map-btn-handle";//地图-处理火情

    public static final String MY_PASSWORD = "app-setting-btn-password";//修改密码
    public static final String MY_POSITION = "app-setting-btn-position";//实时位置上传
    public static final String MY_AUDIO = "app-setting-btn-audio";//语音播报

    public static boolean hasPermission(Context context, String code){
        String mt = (String) SPUtils.get(context, SPValue.permission, "");
        return mt.contains(code);
    }
}
