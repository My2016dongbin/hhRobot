package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
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
import com.ehaohai.robot.ui.activity.DeviceListActivity;
import com.ehaohai.robot.ui.activity.LoginActivity;
import com.ehaohai.robot.ui.activity.OfflineLoginActivity;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;

public class OfflineLoginViewModel extends BaseViewModel {
    public Context context;
    public String sn;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public boolean eye = false;

    public void start(Context context) {
        this.context = context;
    }

    public void login(String userName, String password) {
        loading.setValue(new LoadingEvent(true, "认证中.."));
        JSONObject object = new JSONObject();
        try {
            object.put("username", userName);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "onSuccess: login = " + object);
        try{
            HhHttp.postString()
                    .url(URLConstant.OFFLINE_LOGIN())
                    .content(object.toString())
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(this, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            Log.e("TAG", "onSuccess: login = " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject data = jsonObject.getJSONObject("data");
                                try{
                                    CommonData.token = data.getString("access_token");
                                    SPUtils.put(context, SPValue.token, CommonData.token);
                                    SPUtils.put(context, SPValue.offlineIp,URLConstant.LOCAL_IP);
                                    CommonUtil.saveFileToken();//Token防丢 测试用
                                    CommonData.sn = sn;
                                    SPUtils.put(HhApplication.getInstance(), SPValue.sn, sn);
                                    ///创建设备本地文件目录
                                    createFiles(sn,userName,password);
                                    SPUtils.put(HhApplication.getInstance(), SPValue.login, true);
                                    msg.setValue("认证成功");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setValue(new LoadingEvent(false, ""));
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            ((OfflineLoginActivity)context).finish();
                                        }
                                    }, 500);
                                }catch (Exception e){
                                    loading.setValue(new LoadingEvent(false));
                                    String message = data.getString("msg");
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
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
                            if(e.toString().contains("Timeout")){
                                Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(context, "认证地址不可用，请重新输入", Toast.LENGTH_SHORT).show();
            loading.setValue(new LoadingEvent(false, ""));
        }
    }

    private void createFiles(String snCode,String userName,String password) {
        // 创建目录
        File deviceDir = new File(context.getCacheDir()+"/device", snCode);
        if (!deviceDir.exists()) deviceDir.mkdirs();
        File picture = new File(context.getCacheDir()+"/device"+"/"+snCode, "picture");
        if (!picture.exists()) picture.mkdirs();
        File face = new File(context.getCacheDir()+"/device"+"/"+snCode, "face");
        if (!face.exists()) face.mkdirs();
        File recordings = new File(context.getCacheDir()+"/device"+"/"+snCode, "recordings");
        if (!recordings.exists()) recordings.mkdirs();
        try {
            // 创建 config 对象
            JSONObject configObject = new JSONObject();

            // 创建主 JSON 对象
            JSONObject root = new JSONObject();
            root.put("ip", URLConstant.LOCAL_IP);
            root.put("sn", snCode);
            root.put("name", "浩海机器狗");
            root.put("account", userName);
            root.put("password", password);
            root.put("token", CommonData.token);
            root.put("config", configObject); // 空对象，可后续添加内容

            // 写入文件
            File configFile = new File(deviceDir, "config.json");
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                fos.write(root.toString(4).getBytes(StandardCharsets.UTF_8)); // 缩进4空格
            }

            HhLog.e("Config", "配置文件已创建: " + configFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            HhLog.e("Config", "创建配置文件失败: " + e.getMessage());
        }
    }

    public void loginOut() {
        Log.e("TAG", "onSuccess: OFFLINE_LOGIN_OUT = " + URLConstant.OFFLINE_LOGIN_OUT());
        HhHttp.post()
                .url(URLConstant.OFFLINE_LOGIN_OUT())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: OFFLINE_LOGIN_OUT = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

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
