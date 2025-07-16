package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class TaskControlViewModel extends BaseViewModel {
    public Context context;
    public Task task = new Task();
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void postTaskToDog(){
        loading.setValue(new LoadingEvent(true,"正在下发任务.."));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","route");
            jsonObject.put("cmd","route_download");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param",new Gson().toJson(task));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "onSuccess: TASK_COMMAND = " + URLConstant.TASK_COMMAND());
        Log.e("TAG", "onSuccess: TASK_COMMAND = " + jsonObject);
        Log.e("TAG", "onSuccess: TASK_COMMAND = " + CommonData.token);

        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            startTask();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void startTask() {
        loading.postValue(new LoadingEvent(true,"正在开始任务.."));
        JSONObject object = new JSONObject();
        try {
            object.put("type","task");
            object.put("cmd","start");
            object.put("seq",new Random().nextInt(10000));
            object.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "任务开始成功", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void pauseTask() {
        loading.postValue(new LoadingEvent(true,"正在暂停任务.."));
        JSONObject object = new JSONObject();
        try {
            object.put("type","task");
            object.put("cmd","end");
            object.put("seq",new Random().nextInt(10000));
            object.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "任务结束成功", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void stopTask() {
        loading.postValue(new LoadingEvent(true,"正在结束任务.."));
        JSONObject object = new JSONObject();
        try {
            object.put("type","task");
            object.put("cmd","end");
            object.put("seq",new Random().nextInt(10000));
            object.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(object.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "任务结束成功", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }
}
