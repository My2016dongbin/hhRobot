package com.ehaohai.robot.utils;

import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class HhUtil {
    /**
     *  01转是否   0否 1是
     * 请选择转""
     */
    public static String intChangeBoolean(String isChoose){
        if (isChoose==null){
            return "请选择";
        }else {
            if (isChoose.equals("1")){
                return "是";
            }else if (isChoose.equals("0")){
                return "否";
            }else {
                return "请选择";
            }
        }

    }
    /**
     * 是否转 01   0否 1是
     * 请选择转""
     */
    public static String booleanChange(String isChoose){
        if (isChoose.equals("是")){
            return "1";
        }else if (isChoose.equals("否")){
            return "0";
        }else {
            return "";
        }
    }

    /**
     * true false转 01   false0 true1
     * 请选择转""
     */
    public static String booleanTFChange12(Boolean isChoose){
        if (isChoose){
            return "1";
        }else  {
            return "0";
        }
    }
    /**
     * 判断字符串是不是 null 是返回""
     *
     * @return
     */
    public static String strIsNull(String str) {
        return str == null?"":str;
    }
    /**
     * 判断字符串是不是 null 是返回""
     *
     * @return
     */
    public static String strIsChoose(String str) {
        return str.equals("请选择")?"":str;
    }
    /**
     * 格式化时间戳
     *
     * @return
     */
    public static String initData(String data) {
        return data.replace("T"," ").substring(0,data.indexOf("."));
    }
    /**
     * 包括空格判断
     *
     * @param input
     * @return
     */
    public static boolean containSpace(CharSequence input) {
        return Pattern.compile("\\s+").matcher(input).find();
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }
    /**
     *  计算两个日期之间的天数
     *
     */
    public static int dayCount(String data1,String data2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(data1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(data2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int dayCount = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 3600 * 24));// 从间隔毫秒变成间隔天数
        return dayCount;
    }


    /**
     * 验证密码是否是数字与字母的组合
     *
     * @param password 密码
     * @return 返回值
     */
    public static boolean isPassword(String password) {
        boolean ispass = false;
        Log.i("TAG","password.matches(\"[0-9]{1,}\")"+password.matches("^[0-9]+$"));
        Log.i("TAG","Pattern.matches(\".*[a-z][A-Z]+.*\", password))"+Pattern.matches(".*[a-z][A-Z]+.*", password));
        Log.i("TAG","password.matches(\".*[~!@#$%^&*()_+|<>,.?/:;'\\\\[\\\\]{}\\\"]+.*\")"+password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*"));
        //6-16至少有一个数字
        if (password.matches("^[0-9]+$")) {

            //包含字母
            if (Pattern.matches(".*[a-z][A-Z]+.*", password)) {

                ispass = true;
            } else {
                if (Pattern.matches(".*[a-z][A-Z]+.*", password)) {
                    //包含字母
                    if (password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*")) {
                        ispass = true;
                    }
                } else {
                    ispass = false;
                }

            }
        }else {
            //包含字母
            if (Pattern.matches(".*[a-z][A-Z]+.*", password)) {
                ispass = password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*");
            }else {
                ispass = false;
            }
        }
        return ispass;
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     *
     * @param s
     * @return
     */
    public boolean check(String s) {
        boolean b = false;
        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }
        return b;

    }


    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}
