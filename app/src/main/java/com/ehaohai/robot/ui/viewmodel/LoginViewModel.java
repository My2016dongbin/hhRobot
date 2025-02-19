package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;


import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.permission.CommonPermission;
import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.ui.activity.LoginActivity;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;

public class LoginViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void getName() {
        HhHttp.get()
                .url(URLConstant.GET_NAME)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: GET_NAME = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length()>0) {
                                JSONObject model = (JSONObject) data.get(0);
                                String loginSystemName = model.getString("loginSystemName");
                                if(!loginSystemName.isEmpty()){
                                    name.postValue(loginSystemName);
                                }
                            }
                            name.postValue(jsonObject.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());//400 密码错误  401 账号未注册
                    }
                });
    }

    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "登录中.."));
        HhHttp.get()
                .url(URLConstant.GET_LOGIN)
                .addParams("username", userName)
                .addParams("password", password)
                .addParams("grant_type", "password")
                .addParams("client_id", "client_password")
                .addParams("client_secret", "123456")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: login = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CommonData.token = jsonObject.getString("access_token");
                            SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                            SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                            SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                            getUserInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());//400 密码错误  401 账号未注册
                        if (Objects.requireNonNull(e.getMessage()).contains("400")) {
                            msg.setValue("账号或密码错误");
                        } else if (Objects.requireNonNull(e.getMessage()).contains("401")) {
                            msg.setValue("账号未注册");
                        } else {
                            msg.setValue("");
                        }
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    private void getUserInfo() {
        HhHttp.get()
                .url(URLConstant.GET_USER_INFO)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: userInfo = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                JSONObject userJsonObj = data.getJSONObject(0);
                                SPUtils.put(HhApplication.getInstance(), SPValue.id, userJsonObj.getString("id"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.userCode, userJsonObj.getString("userCode"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.fullName, userJsonObj.getString("fullName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.phone, userJsonObj.getString("phone"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.email, userJsonObj.getString("email"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupId, userJsonObj.getString("groupId"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.gridNo, userJsonObj.getString("gridNo"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.groupName, userJsonObj.getString("groupName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.headUrl, userJsonObj.getString("headUrl"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.roleName, userJsonObj.getString("roleName"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.state, userJsonObj.getString("state"));
                                SPUtils.put(HhApplication.getInstance(), SPValue.manager, true);

                                SPUtils.put(HhApplication.getInstance(), SPValue.login, true);

                                Set<String> tagSet = new LinkedHashSet<>();
                                tagSet.add(userJsonObj.getString("id"));
                                tagSet.add(userJsonObj.getString("gridNo"));
                                //tagSet.add(userJsonObj.getString("groupId"));
                                tagSet.add("wwyt_" + userJsonObj.getString("groupId"));
                                //tagSet.add(CommonData.pushFlag);
                                tagSet.add("wwyt_20240205");
                                XGPushManager.setTags(context, "setTag", tagSet);


                                getUserPermission();

                                /*msg.setValue("登录成功");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setValue(new LoadingEvent(false, ""));
                                        context.startActivity(new Intent(context, MainActivity.class));
                                        ((LoginActivity)context).finish();
                                    }
                                }, 500);*/

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    private void getUserPermission() {
        loading.setValue(new LoadingEvent(true, "正在获取权限.."));
        //获取主菜单权限
        HhHttp.get()
                .url(URLConstant.PERMISSION_MAIN)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: PERMISSION_MAIN = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject obj = (JSONObject) data.get(0);
                            JSONArray dataList = obj.getJSONArray("menuDTOS");
                            CommonData.hasMainMap = false;
                            CommonData.hasMainVideo = false;
                            CommonData.hasMainApp = false;
                            CommonData.hasMainMy = false;
                            CommonData.hasMainMessage = false;
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject o = (JSONObject) dataList.get(i);
                                String menuCode = o.getString("menuCode");
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MAP)) {
                                    CommonData.hasMainMap = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_VIDEO)) {
                                    CommonData.hasMainVideo = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_APP)) {
                                    CommonData.hasMainApp = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MY)) {
                                    CommonData.hasMainMy = true;
                                }
                                if (Objects.equals(menuCode, CommonPermission.MAIN_MESSAGE)) {
                                    CommonData.hasMainMessage = true;
                                }
                            }
                            //强制显示'我的'菜单
                            CommonData.hasMainMy = true;
                            SPUtils.put(context, SPValue.hasMainApp, CommonData.hasMainApp);
                            SPUtils.put(context, SPValue.hasMainVideo, CommonData.hasMainVideo);
                            SPUtils.put(context, SPValue.hasMainMessage, CommonData.hasMainMessage);
                            SPUtils.put(context, SPValue.hasMainMap, CommonData.hasMainMap);
                            SPUtils.put(context, SPValue.hasMainMy, CommonData.hasMainMy);
                            int appIndex = 0;
                            int videoIndex = 1;
                            int messageIndex = 2;
                            int mapIndex = 3;
                            int myIndex = 4;
                            int index = 0;
                            if(CommonData.hasMainApp){
                                appIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainVideo){
                                videoIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMessage){
                                messageIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMap){
                                mapIndex = index;
                                index++;
                            }
                            if(CommonData.hasMainMy){
                                myIndex = index;
                            }
                            SPUtils.put(context, SPValue.appIndex, appIndex);
                            SPUtils.put(context, SPValue.videoIndex, videoIndex);
                            SPUtils.put(context, SPValue.messageIndex, messageIndex);
                            SPUtils.put(context, SPValue.mapIndex, mapIndex);
                            SPUtils.put(context, SPValue.myIndex, myIndex);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                    }
                });


        permissionStr = "";
        String[] menuCodeList = {CommonPermission.MAIN_APP,CommonPermission.MAIN_VIDEO,CommonPermission.MAIN_MESSAGE,CommonPermission.MAIN_MAP,CommonPermission.MAIN_MY};
        for (int i = 0; i < menuCodeList.length; i++) {
            getInnerPermission(menuCodeList[i]);
        }
    }

    private String permissionStr = "";
    private int permissionCount = 0;

    private void getInnerPermission(String code) {
        //获取子菜单权限
        HhHttp.get()
                .url(URLConstant.PERMISSION_PER)
                .addParams("menuCode", code)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: PERMISSION_PER = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray dataList = object.getJSONArray("data");
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject o = (JSONObject) dataList.get(i);
                                String code = o.getString("elementCode");
                                String p = code + "_";//分割符_
                                if (permissionStr != null && !permissionStr.isEmpty()) {
                                    permissionStr = permissionStr + "," + p;
                                } else {
                                    permissionStr = p;
                                }
                            }
                            permissionCount++;
                            if (permissionCount == 5) {
                                try {
                                    SPUtils.put(context, SPValue.permission, permissionStr);
                                    String mt = (String) SPUtils.get(context, SPValue.permission, "");
                                    HhLog.e("读取权限 PERMISSION_ " + CommonData.hasMainApp + CommonData.hasMainVideo + CommonData.hasMainMessage + CommonData.hasMainMap + CommonData.hasMainMy  + mt);

                                    msg.setValue("登录成功");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setValue(new LoadingEvent(false, ""));
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            ((LoginActivity) context).finish();
                                        }
                                    }, 500);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "权限获取异常", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "权限获取异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                    }
                });
    }

}
