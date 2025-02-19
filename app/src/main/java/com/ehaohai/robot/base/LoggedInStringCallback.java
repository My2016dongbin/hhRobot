package com.ehaohai.robot.base;

import android.content.Context;

import com.ehaohai.robot.utils.HhLog;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

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
        onFailure(call, e, id);
    }

    @Override
    public void onResponse(String response, int id) {
        /*if ("102".equals(JSON.parseObject(response).getString("errcode"))) {//登录失效
//            mViewModel.loading.setValue(new LoadingEvent(false));
//            mViewModel.loginAgain.setValue(new Object());
//            mViewModel.showLoginErrorDialog();
//            LogUtils.e("zcm","重新登录111");
            context.sendBroadcast(new Intent().setAction(SPValue.TOKEN_FAILURE));
        } else {
            onSuccess(response, id);
        }*/
        onSuccess(response, id);
    }

    public abstract void onSuccess(String response, int id);

    public abstract void onFailure(Call call, Exception e, int id);
}
