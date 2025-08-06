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
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import okhttp3.Call;

public class PointModeViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public final MutableLiveData<Boolean> start = new MutableLiveData<>(false);
    public double angleLeft = 999;
    public double angleRight = 999;
    public long times = 0;

    public double vxPost = 0;
    public double vyPost = 0;
    public double vyawPost = 0;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sportControl("manual","run",object.toString());
    }

    public void startPoint() {
        loading.setValue(new LoadingEvent(true));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","point");
            jsonObject.put("cmd","start");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param","");
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
                .connTimeOut(60000)  // 设置连接超时
                .readTimeOut(60000)  // 设置读取超时（可选）
                .writeTimeOut(60000) // 设置写入超时（可选）
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "已开启打点模式", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: PointMode" + e.toString());
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    public void addPoint(String param) {
        loading.setValue(new LoadingEvent(true));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","point");
            jsonObject.put("cmd","add");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param",param);
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
                            Toast.makeText(context, "点位添加成功", Toast.LENGTH_SHORT).show();

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

    public void stopPoint(String param,Action click) {
        loading.setValue(new LoadingEvent(true));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","point");
            jsonObject.put("cmd","end");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param",param);
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
                .connTimeOut(60000)  // 设置连接超时
                .readTimeOut(60000)  // 设置读取超时（可选）
                .writeTimeOut(60000) // 设置写入超时（可选）
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "已退出打点模式，所有打点保存成功", Toast.LENGTH_SHORT).show();
                            click.click();

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
