package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.utils.HhLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class ModeViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<String> mode = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;

    }

    public void sportControl(String type,String cmd,String param) {
        JSONObject object = new JSONObject();
        try {
            object.put("type", type);
            object.put("cmd", cmd);
            object.put("seq", new Random().nextInt(10000));
            object.put("param", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "onSuccess: OFFLINE_CONTROL = " + URLConstant.OFFLINE_CONTROL() + object);
        HhHttp.postString()
                .url(URLConstant.OFFLINE_CONTROL())
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: OFFLINE_CONTROL = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                        } catch (Exception e) {
                            e.printStackTrace();
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
