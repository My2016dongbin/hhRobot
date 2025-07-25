package com.ehaohai.robot.ui.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.model.Heart;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.wifi.UDPReceiver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.Call;

public class PersistentForegroundService extends Service {

    private static final String TAG = "ForegroundService";
    private static final String CHANNEL_ID = "qc_service_channel";
    private Handler handler = new Handler();
    private Runnable taskRunnable;
    UDPReceiver udpReceiver;
    public static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        ///UDP消息监听
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 启动接收线程
        udpReceiver = new UDPReceiver(wifiManager);
        new Thread(udpReceiver).start();

        ///心跳服务
        startForegroundService();
        Log.d(TAG, "Foreground service created");

        taskRunnable = new Runnable() {
            @Override
            public void run() {
                heart();
                handler.postDelayed(this, 5_000);
            }
        };

        handler.post(taskRunnable);
    }

    private void heart() {
        Log.e("TAG", "HEART = " + URLConstant.HEART());
        ///参数心跳
        HhHttp.get()
                .url(URLConstant.HEART())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(null, this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: HEART = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataJSONObject = jsonObject.getJSONObject("data");
                            JSONObject data = dataJSONObject.getJSONObject("data");
                            EventBus.getDefault().post(new Heart(data.getString("DeviceSn"),
                                    data.getDouble("ambTemperature"),
                                    data.getDouble("ambHumidity"),
                                    data.getDouble("linearSpeed"),
                                    data.getInt("batteryPercentage"),
                                    data.getInt("somkeValue")
                                    ));
                            ///刷新任务列表任务状态
                            try{
                                String taskID = data.getString("taskID");
                                if(!Objects.equals(CommonData.taskId, taskID)){
                                    EventBus.getDefault().post(new Task());
                                }
                                CommonData.taskId = taskID;
                            }catch (Exception e){
                             //
                            }
                            ///刷新任务列表任务状态
                            try{
                                String taskStatus = data.getString("taskStatus");
                                if(!Objects.equals(CommonData.taskStatus, taskStatus)){
                                    EventBus.getDefault().post(new Task());
                                }
                                CommonData.taskStatus = taskStatus;
                            }catch (Exception e){
                             //
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            HhLog.e("catch: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                    }
                });
        ///HEART_TOKEN token心跳
        HhHttp.post()
                .url(URLConstant.HEART_TOKEN()+"?id=1")
//                .addParams("id","1")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(null, this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: HEART_TOKEN = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                            HhLog.e("catch: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                    }
                });
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "连接心跳服务",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("连接成功")
            .setContentText("已成功连接机器狗")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Foreground service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        udpReceiver.stop();
        handler.removeCallbacks(taskRunnable);
        Log.d(TAG, "Foreground service destroyed");
        super.onDestroy();
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // 非绑定服务
    }
}
