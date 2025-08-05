package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    public List<Point> routeList = new ArrayList<>();
    public String routeNames = "";
    public List<Object> aiItems = new ArrayList<>();
    public List<Task> taskList = new ArrayList<>();
    public Task task = new Task();
    public int pageNum = 1;

    public void start(Context context) {
        this.context = context;

    }

    public void postAddTaskOnline(String name,String time){
        loading.setValue(new LoadingEvent(true,"正在创建巡检任务.."));
        JSONObject jsonObject = new JSONObject();
        Task task = new Task();
        List<Task.Route> routeArrayList = new ArrayList<>();
        for (int i = 0; i < routeList.size(); i++) {
            Point point = routeList.get(i);
            Task.Route route = new Task.Route();
            route.setPoint_index(Integer.parseInt(point.getId()));
            route.setPoi_name(point.getName());
            route.setPoint_type(point.getType());
            route.setLift_param(point.getTaskFloor());
            route.setX(point.getX());
            route.setY(point.getY());
            route.setZ(point.getZ());
            routeArrayList.add(route);
        }
        task.setTask_id(new Date().getTime()+"");
        task.setTask_name(name);
        task.setStart_time(time);
        task.setTask_timer(hour*60*60);
        task.setTask_route(routeArrayList);
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
        /*
        //创建任务时暂不下发只存储本地
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
                            postAddTaskLocal(task);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });*/

        addTaskLocal(task);
    }
    public void addTaskLocal(Task task){
        CommonUtil.addRobotTask(CommonData.sn,task);
        delayTaskListLocal();
    }

    public void postAndStartTaskOnline(Task taskModel){
        loading.setValue(new LoadingEvent(true,"正在创建巡检任务.."));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","route");
            jsonObject.put("cmd","route_download");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param",new Gson().toJson(taskModel));
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
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    public void startTask() {
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
                            Toast.makeText(context, "下发成功", Toast.LENGTH_SHORT).show();

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

    public void endTaskAndDelete() {
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
                            CommonUtil.deleteRobotTask(CommonData.sn,task);
                            delayTaskListLocal();

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

    public void delayTaskListLocal(){
        loading.setValue(new LoadingEvent(true));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                postTaskListLocal();
                loading.setValue(new LoadingEvent(false));
            }
        },2000);
    }

    public void postTaskListLocal(){
        taskList = CommonUtil.getRobotTask(CommonData.sn);
        HhLog.e("postTaskListLocal", "任务读取 " + taskList.toString());

        updateData();

        postTaskListOnline();
    }
    public void postTaskListOnline(){
        loading.setValue(new LoadingEvent(true));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","route");
            jsonObject.put("cmd","route_upload");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "onSuccess: TASK_COMMAND list = " + URLConstant.TASK_COMMAND());
        Log.e("TAG", "onSuccess: TASK_COMMAND list = " + jsonObject);
        Log.e("TAG", "onSuccess: TASK_COMMAND list = " + CommonData.token);
        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND list = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject param = jsonObject.getJSONObject("param");
                            JSONObject route_json = param.getJSONObject("route_json");
                            Task taskOnline = new Gson().fromJson(route_json.toString(), new TypeToken<Task>(){}.getType());
                            if(taskList.size()==0){
                                taskList.add(taskOnline);
                                updateData();
                            }else{
                                int tag = 0;
                                for (int i = 0; i < taskList.size(); i++) {
                                    Task task = taskList.get(i);
                                    if(Objects.equals(task.getTask_id(), taskOnline.getTask_id())){
                                        return;
                                    }else{
                                        tag++;
                                    }
                                }
                                if(tag >= taskList.size()){
                                    taskList.add(taskOnline);
                                    updateData();
                                }
                            }


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
