package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.Call;

public class AccountSafeViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void loginOut() {
        CommonData.loginDownLong = new Date().getTime();
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
                            ///清空账号数据
                            CommonUtil.accountClearNoJump();

                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.setValue(new LoadingEvent(false));
                            //Toast.makeText(context, "服务异常", Toast.LENGTH_SHORT).show();
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
