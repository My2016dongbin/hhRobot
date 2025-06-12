package com.ehaohai.robot.base;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ehaohai.robot.ui.activity.LaunchActivity;
import com.ehaohai.robot.ui.activity.LoginActivity;
import com.ehaohai.robot.ui.activity.MineActivity;
import com.ehaohai.robot.ui.service.PersistentForegroundService;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

public abstract class LoggedInStringCallback extends Callback<String> {
    private final BaseViewModel mViewModel;
    private final Context context;

    public LoggedInStringCallback(BaseViewModel viewModel, Context context) {
        this.mViewModel = viewModel;
        this.context = context;
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        HhLog.e("onError:" + e.toString());
        Date date = new Date();
        long now = date.getTime();
        if(now - CommonData.loginDownLong < 2000){
            return;
        }
        CommonData.loginDownLong = now;
        if (e.toString().contains("401")) {
            ///登录失效
            Toast.makeText(context, "登录信息失效，请重新登录", Toast.LENGTH_SHORT).show();
            CommonUtil.accountClear();
        }
        onFailure(call, e, id);
    }

    @Override
    public void onResponse(String response, int id) {
        if (response.contains(":401,")) {
            Toast.makeText(context, "登录信息失效，请重新登录", Toast.LENGTH_SHORT).show();
            CommonUtil.accountClear();
        } else {
            ///正常返回
            onSuccess(response, id);
        }
    }

    public abstract void onSuccess(String response, int id);

    public abstract void onFailure(Call call, Exception e, int id);
}
