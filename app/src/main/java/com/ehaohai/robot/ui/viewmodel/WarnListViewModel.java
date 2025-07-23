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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class WarnListViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<Integer> aiNum = new MutableLiveData<>();
    public final MutableLiveData<Integer> bugNum = new MutableLiveData<>();
    public final MutableLiveData<String> todayCount = new MutableLiveData<>();
    public final MutableLiveData<String> allCount = new MutableLiveData<>();
    public MultiTypeAdapter aiAdapter;
    public MultiTypeAdapter bugAdapter;
    public List<Object> aiItems = new ArrayList<>();
    public List<Object> bugItems = new ArrayList<>();
    public List<Warn> warnList = new ArrayList<>();
    public List<Warn> bugList = new ArrayList<>();
    public boolean isAi = true;
    public int pageNumAi = 1;
    public int pageNumBug = 1;

    public void start(Context context) {
        this.context = context;

    }

    public void postWarnList(){
        getAiUnRead();
        HhHttp.get()
                .url(URLConstant.ALARM_LIST())
                .addParams("pageNum",pageNumAi+"")
                .addParams("pageSize","10")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ALARM_LIST = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray items = data.getJSONArray("items");
                            warnList = new Gson().fromJson(items.toString(), new TypeToken<List<Warn>>(){}.getType());
                            for (int i = 0; i < warnList.size(); i++) {
                                warnList.get(i).setCount(i);
                            }
                            updateDataAi();

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


        ///当日报警总数
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        HhHttp.get()
                .url(URLConstant.ALARM_DAY_COUNT())
                .addParams("start_time",format)
                .addParams("end_time",format)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ALARM_DAY_COUNT = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            JSONArray array = (JSONArray) data.get(0);
                            todayCount.postValue(array.get(1)+"");
                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.setValue(new LoadingEvent(false));
                            HhLog.e("ALARM_DAY_COUNT " + e);
                            Toast.makeText(context, "服务异常", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });

        ///总报警数
        HhHttp.get()
                .url(URLConstant.ALARM_ALL_COUNT())
//                .addParams("start_time","")
//                .addParams("end_time","")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ALARM_ALL_COUNT = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            allCount.postValue(data);

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
    public void postBugList(){
        getErrorUnRead();
        HhHttp.get()
                .url(URLConstant.ERROR_LIST())
                .addParams("pageNum",pageNumBug+"")
                .addParams("pageSize","10")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ERROR_LIST = " + pageNumBug);
                        Log.e("TAG", "onSuccess: ERROR_LIST = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray items = data.getJSONArray("items");
                            bugList = new Gson().fromJson(items.toString(), new TypeToken<List<Warn>>(){}.getType());
                            for (int i = 0; i < bugList.size(); i++) {
                                bugList.get(i).setCount(i);
                            }
                            updateDataBug();

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


    @SuppressLint("NotifyDataSetChanged")
    public void updateDataAi() {
        if(pageNumAi==1){
            aiItems.clear();
        }
        if (warnList != null && warnList.size()!=0) {
            aiItems.addAll(warnList);
        }else{
            aiItems.add(new Empty());
        }

        assertAllRegistered(aiAdapter, aiItems);
        aiAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataBug() {
        if(pageNumBug==1){
            bugItems.clear();
        }
        if (bugList != null && bugList.size()!=0) {
            bugItems.addAll(bugList);
        }else{
            bugItems.add(new Empty());
        }

        assertAllRegistered(bugAdapter, bugItems);
        bugAdapter.notifyDataSetChanged();
    }

    public void getAlarmRead(Warn warn) {
        HhHttp.get()
                .url(URLConstant.AI_READ())
                .addParams("pk",warn.getId())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: AI_READ = " + warn.getId());
                        Log.e("TAG", "onSuccess: AI_READ = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            pageNumAi = 1;
                            Toast.makeText(context, "成功标记已读", Toast.LENGTH_SHORT).show();
                            postWarnList();

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
    public void getBugRead(Warn warn) {
        HhHttp.get()
                .url(URLConstant.ERROR_READ())
                .addParams("pk",warn.getId())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ERROR_READ = " + warn.getId());
                        Log.e("TAG", "onSuccess: ERROR_READ = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            pageNumBug = 1;
                            Toast.makeText(context, "成功标记已读", Toast.LENGTH_SHORT).show();
                            postBugList();

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

    public void getAiUnRead() {
        HhHttp.get()
                .url(URLConstant.ALARM_UNREAD())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ALARM_UNREAD = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            int unread_alarm_num = data.getInt("unread_alarm_num");

                            aiNum.postValue(unread_alarm_num);

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
    public void getErrorUnRead() {
        HhHttp.get()
                .url(URLConstant.ERROR_UNREAD())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: ERROR_UNREAD = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            int unread_alarm_num = data.getInt("unread_alarm_num");
                            bugNum.postValue(unread_alarm_num);

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
