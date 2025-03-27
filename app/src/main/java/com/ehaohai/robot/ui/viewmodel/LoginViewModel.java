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
    public boolean confirm = false;

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
        JSONObject object = new JSONObject();
        try {
            object.put("username", userName);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "onSuccess: login = " + object);
        HhHttp.postString()
                .url(URLConstant.OFFLINE_LOGIN())
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: login = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            try{
                                CommonData.token = data.getString("access_token");
                                SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                                SPUtils.put(HhApplication.getInstance(), SPValue.userName, userName);
                                SPUtils.put(HhApplication.getInstance(), SPValue.password, password);
                                SPUtils.put(HhApplication.getInstance(), SPValue.login, true);
                                msg.setValue("登录成功");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setValue(new LoadingEvent(false, ""));
                                        context.startActivity(new Intent(context, MainActivity.class));
                                        ((LoginActivity)context).finish();
                                    }
                                }, 500);
                            }catch (Exception e){
                                loading.setValue(new LoadingEvent(false));
                                String message = data.getString("msg");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.setValue(new LoadingEvent(false));
                            Toast.makeText(context, "服务异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    public void loginOut() {
        Log.e("TAG", "onSuccess: OFFLINE_LOGIN_OUT = " + URLConstant.OFFLINE_LOGIN_OUT());
        HhHttp.post()
                .url(URLConstant.OFFLINE_LOGIN_OUT())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: OFFLINE_LOGIN_OUT = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.setValue(new LoadingEvent(false));
                            Toast.makeText(context, "服务异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

}
