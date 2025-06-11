package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.activity.LoginActivity;
import com.ehaohai.robot.ui.activity.OfflineLoginActivity;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class OfflineLoginViewModel extends BaseViewModel {
    public Context context;
    public String sn;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public boolean eye = false;

    public void start(Context context) {
        this.context = context;
    }

    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "认证中.."));
        JSONObject object = new JSONObject();
        try {
            object.put("username", userName);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "onSuccess: login = " + object);
        try{
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
                                    CommonData.sn = sn;
                                    SPUtils.put(HhApplication.getInstance(), SPValue.sn, sn);
                                    /*SPUtils.put(HhApplication.getInstance(), SPValue.userNameOff, userName);
                                    SPUtils.put(HhApplication.getInstance(), SPValue.passwordOff, password);*/
                                    SPUtils.put(HhApplication.getInstance(), SPValue.login, true);
                                    msg.setValue("认证成功");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setValue(new LoadingEvent(false, ""));
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            ((OfflineLoginActivity)context).finish();
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
                            if(e.toString().contains("Timeout")){
                                Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(context, "认证地址不可用，请重新输入", Toast.LENGTH_SHORT).show();
            loading.setValue(new LoadingEvent(false, ""));
        }
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
