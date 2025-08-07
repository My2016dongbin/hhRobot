package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
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
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;

public class ControlViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public boolean shenLanYao = false;
    public boolean biXin = false;
    public boolean puRen = false;
    public boolean jump = false;
    public boolean wuDao1 = false;
    public boolean wuDao2 = false;

    public boolean zuNi = false;
    public boolean zhanLi = false;
    public boolean zuoXia = false;
    public boolean woDao = false;
    public boolean lock = false;
    public boolean daZhaoHu = false;

    public boolean cloudSet = false;

    public final MutableLiveData<Boolean> force = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> pan = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> data = new MutableLiveData<>(false);
    public boolean speak = false;
    public boolean voice = false;
    public boolean record = false;
    public final MutableLiveData<String> recordTimes = new MutableLiveData<>();
    public final MutableLiveData<String> voiceTimes = new MutableLiveData<>();
    public boolean stop = false;
    public int stopDistance = 160;
    public boolean isDog = true;
    public Calendar date;
    public Calendar dateVoice;
    public double angleLeft = 999;
    public double angleRight = 999;
    public double angleCloud = 999;
    public long times = 0;
    public long timesCloud = 0;
    public static final int REQUEST_PERMISSION_CODE = 100;
    public MediaRecorder mediaRecorder;
    public String fileName;
    public String outputFilePath;

    public double vxPost = 0;
    public double vyPost = 0;
    public double vyawPost = 0;

    public double speed1 = 0;//水平旋转速度，-1:向右转动，1：向左转动
    public double speed2 = 0;//俯仰旋转速度，-1:向下转动，1：向上转动

    public void start(Context context) {
        this.context = context;
    }

    public void startRecordTimes() {
        date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.SECOND,0);
        recordTimes.postValue(CommonUtil.parseZero(date.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(date.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(date.get(Calendar.SECOND)));
        runTimes();
    }

    public void startRecordTimesVoice() {
        dateVoice = Calendar.getInstance();
        dateVoice.set(Calendar.HOUR_OF_DAY,0);
        dateVoice.set(Calendar.MINUTE,0);
        dateVoice.set(Calendar.SECOND,0);
        voiceTimes.postValue(CommonUtil.parseZero(dateVoice.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(dateVoice.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(dateVoice.get(Calendar.SECOND)));
        runTimesVoice();
    }

    public void uploadAudio() {
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        HhLog.e("onSuccess: post UPLOAD_AUDIO " + URLConstant.UPLOAD_AUDIO());
        HhHttp.post()
                .url(URLConstant.UPLOAD_AUDIO())
                .addFile("file",  fileName,new File(outputFilePath))
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post UPLOAD_AUDIO " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                //Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                                postPlay();
                            } else {
                                //Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void postPlay() {
        JSONObject jsonObject = new JSONObject();
        List<String> stringList = new ArrayList<>();
        stringList.add(fileName);
        RequestParams params = new RequestParams(URLConstant.PLAY_AUDIO());
//        params.addParameter("command",1);
        try {
            jsonObject.put("type","aplay");
            jsonObject.put("cmd","SP");
            jsonObject.put("seq",0);
            jsonObject.put("param",new Gson().toJson(stringList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setBodyContent(jsonObject.toString());
        HhLog.e("onSuccess: post PLAY_AUDIO " + URLConstant.PLAY_AUDIO());
        HhLog.e("onSuccess: post PLAY_AUDIO " + jsonObject);
        HhLog.e("onSuccess: post PLAY_AUDIO " + CommonData.token);
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("onSuccess: post PLAY_AUDIO " + result);
                Toast.makeText(context, "已发送", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e("onFailure: PLAY_AUDIO " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
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

    private void runTimes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                date.add(Calendar.SECOND,1);
                recordTimes.postValue(CommonUtil.parseZero(date.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(date.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(date.get(Calendar.SECOND)));
                if(record){
                    runTimes();
                }
            }
        },1000);
    }

    private void runTimesVoice() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dateVoice.add(Calendar.SECOND,1);
                voiceTimes.postValue(CommonUtil.parseZero(dateVoice.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(dateVoice.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(dateVoice.get(Calendar.SECOND)));
                if(voice){
                    runTimesVoice();
                }
            }
        },1000);
    }

    public void stopRecordTimes() {
        record = false;
    }

    public void stopVoiceTimes() {
        voice = false;
    }

    public void controlParse() {
        long timeNow = Calendar.getInstance().getTime().getTime();
        if(timeNow - times < 200){
            return;
        }
        times = timeNow;
        double vx = 0;
        double vy = 0;
        double vyaw = 0;
        if(angleLeft >= 135 && angleLeft <= 225){
            vx = -0.5;
        }
        if((angleLeft > 90 && angleLeft < 135) || (angleLeft > 225 && angleLeft < 270)){
            if(angleLeft > 90 && angleLeft < 135){
                vx = -0.25 * (angleLeft-90)/(135-90);
            }
            if(angleLeft > 225 && angleLeft < 270){
                vx = -0.25 * (270-angleLeft)/(270-225);
            }
        }
        if((angleLeft >= 0 && angleLeft <= 45) || (angleLeft >= 315 && angleLeft <= 360)){
            vx = 0.5;
        }
        if((angleLeft > 45 && angleLeft < 90) || (angleLeft > 270 && angleLeft < 315)){
            if(angleLeft > 45 && angleLeft < 90){
                vx = 0.25 * (90-angleLeft)/(90-45);
            }
            if(angleLeft > 270 && angleLeft < 315){
                vx = 0.25 * (angleLeft-270)/(315-270);
            }
        }
        if(angleLeft >= 45 && angleLeft <= 135){
            vy = -0.5;
        }
        if((angleLeft > 135 && angleLeft < 180) || (angleLeft > 0 && angleLeft < 45)){
            if(angleLeft > 135 && angleLeft < 180){
                vy = -0.25 * (180-angleLeft)/(180-138);
            }
            if(angleLeft > 0 && angleLeft < 45){
                vy = -0.25 * (angleLeft-0)/(45-0);
            }
        }
        if(angleLeft >= 225 && angleLeft <= 315){
            vy = 0.5;
        }
        if((angleLeft > 180 && angleLeft < 225) || (angleLeft > 315 && angleLeft < 360)){
            if(angleLeft > 180 && angleLeft < 225){
                vy = -0.25 * (angleLeft-180)/(225-180);
            }
            if(angleLeft > 315 && angleLeft < 360){
                vy = -0.25 * (315-angleLeft)/(360-315);
            }
        }
        if(angleLeft == 90 || angleLeft == 270){
            vx = 0;
        }
        if(angleLeft == 0 || angleLeft == 180 || angleLeft ==360){
            vy = 0;
        }
        if(angleLeft == 999){
            vx = 0;
            vy = 0;
        }


        if(angleRight >= 135 && angleRight <= 225){
            vyaw = -0.5;
        }
        if((angleRight > 90 && angleRight < 135) || (angleRight > 225 && angleRight < 270)){
            vyaw = -0.25;
        }
        if((angleRight >= 0 && angleRight <= 45) || (angleRight >= 315 && angleRight <= 360)){
            vyaw = 0.5;
        }
        if((angleRight > 45 && angleRight < 90) || (angleRight > 270 && angleRight < 315)){
            vyaw = 0.25;
        }
        if(angleRight == 90 || angleRight == 270){
            vyaw = 0;
        }
        if(angleRight == 999){
            vyaw = 0;
        }

        if(Math.abs(vx) < 0.25){
            vxPost = 0;
        }else{
            vxPost = vx;
        }
        if(Math.abs(vy) < 0.25){
            vyPost = 0;
        }else{
            vyPost = vy;
        }
        vyawPost = vyaw;
        /*vxPost = vx;
        vyPost = vy;
        vyawPost = vyaw;*/
    }

    public void controlPost(){
        JSONObject object = new JSONObject();
        try {
            object.put("vx",CommonUtil.parseDoubleCount(vxPost));
            object.put("vy",CommonUtil.parseDoubleCount(vyPost));
            object.put("vyaw",CommonUtil.parseDoubleCount(vyawPost));
            object.put("timestamp",Calendar.getInstance().getTime().getTime()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sportControl("manual","run",object.toString());
    }

    public void controlPostCloud(){
        List<Double> list = new ArrayList<>();
        list.add(speed1);
        list.add(speed2);
        sportControl("manual","CameraSpeed",list.toString());
    }

    public void controlCloudParse() {
        long timeNow = Calendar.getInstance().getTime().getTime();
        if(timeNow - timesCloud < 200){
            return;
        }
        timesCloud = timeNow;

        if(angleCloud >= 225 && angleCloud <= 315){
            //向上
            speed2 = 1;
            speed1 = 0;
            HhLog.e("Cloud move Up");
        }
        if((angleCloud > 315 && angleCloud <= 360)  ||  (angleCloud >= 0 && angleCloud < 45)){
            //向右
            speed1 = -1;
            speed2 = 0;
            HhLog.e("Cloud move Right");
        }
        if(angleCloud >= 45 && angleCloud <= 135){
            //向下
            speed2 = -1;
            speed1 = 0;
            HhLog.e("Cloud move Down");
        }
        if(angleCloud > 135 && angleCloud < 225){
            //向左
            speed1 = 1;
            speed2 = 0;
            HhLog.e("Cloud move Left");
        }
        if(angleCloud == 999){
            //没动
            speed1 = 0;
            speed2 = 0;
            HhLog.e("Cloud move Don't move");
        }


        /*JSONObject object = new JSONObject();
        try {
            object.put("vx",CommonUtil.parseDoubleCount(vx));
            object.put("vy",CommonUtil.parseDoubleCount(vy));
            object.put("vyaw",CommonUtil.parseDoubleCount(vyaw));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sportControl("manual","run",object.toString());*/
    }

}
