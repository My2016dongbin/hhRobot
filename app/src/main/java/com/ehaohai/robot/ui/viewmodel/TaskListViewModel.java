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
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class TaskListViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<String> warn = new MutableLiveData<>();
    //drawerState 侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
    public final MutableLiveData<Integer> drawerState = new MutableLiveData<>(1);
    public final MutableLiveData<Integer> aroundHour = new MutableLiveData<>(2);
    public int hour = 2;
    public MultiTypeAdapter aiAdapter;
    public List<Object> aiItems = new ArrayList<>();
    public List<Task> taskList = new ArrayList<>();
    public int pageNum = 1;

    public void start(Context context) {
        this.context = context;

    }

    public void postTaskList2(){
        loading.setValue(new LoadingEvent(true));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","points");
            jsonObject.put("cmd","points_upload");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    public void postTaskList(){
        postTaskList2();
        taskList = new ArrayList<>();
        taskList.add(new Task("0","一楼巡检","每天","站点1；站点2；站点3；","未开始","08:00:00"));
        taskList.add(new Task("0","二楼巡检","每天","站点1；站点2；站点3；","未开始","08:00:00"));
        taskList.add(new Task("0","三楼巡检","每天","站点2；站点1；站点3；","未开始","08:00:00"));
        updateData();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        if(pageNum==1){
            aiItems.clear();
        }
        if (taskList != null && taskList.size()!=0) {
            aiItems.addAll(taskList);
        }else{
            aiItems.add(new Empty());
        }

        assertAllRegistered(aiAdapter, aiItems);
        aiAdapter.notifyDataSetChanged();
    }

}
