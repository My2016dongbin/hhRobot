package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.activity.LaunchActivity;
import com.ehaohai.robot.ui.activity.LoginActivity;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class LaunchViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }


    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "自动登录中.."));
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
                        HhLog.e("onSuccess: GET_LOGIN = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CommonData.token = jsonObject.getString("access_token");
                            SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                            SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                            SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    ((LaunchActivity)context).finish();
                                }
                            },2000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SPUtils.put(context,SPValue.login,false);
                                context.startActivity(new Intent(context, LoginActivity.class));
                                ((LaunchActivity)context).finish();
                            }
                        },2000);
                    }
                });
    }
}
