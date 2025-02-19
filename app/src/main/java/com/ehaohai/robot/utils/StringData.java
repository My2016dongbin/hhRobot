package com.ehaohai.robot.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qc
 * on 2023/7/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class StringData {
    public static final String login_name_null = "请输入用户名";
    public static final String login_password_null = "请输入密码";

    public static String parse19(String str) {
        String r = str;
        try{
            r = str.substring(0,19).replace("T"," ");
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        return r;
    }

    public static String parseFirst(String taskImg) {
        String str = taskImg;
        try{
            int i = taskImg.indexOf(",");
            str = taskImg.substring(0,i);
        }catch (Exception e){
            Log.e("TAG", "parseFirst: " + e.getMessage() );
        }
        return str;
    }
    public static String parseSecond(String taskImg) {
        String str = taskImg;
        try{
            int i = taskImg.indexOf(",");
            str = taskImg.substring(i+1);
        }catch (Exception e){
            Log.e("TAG", "parseSecond: " + e.getMessage() );
        }
        return str;
    }
    public static List<String> parseList(String strList) {
        String[] split = strList.split(",");
        return new ArrayList<>(Arrays.asList(split));
    }
}
