package com.ehaohai.robot.ui.viewmodel;

import static com.ehaohai.robot.utils.ImageUtils.rotaingImageView;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.ImageUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class AudioListViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();
    public List<Audio> audioList = new ArrayList<>();
    public MediaRecorder mediaRecorder;
    public String outputFilePath;
    public boolean recording = false;
    public Calendar date;
    public final MutableLiveData<String> recordTimes = new MutableLiveData<>();
    public boolean isPlayingMp3 = false;

    public void start(Context context) {
        this.context = context;
    }

    public void getAudioList() {
        audioList = new ArrayList<>();
        File dir = new File(context.getCacheDir()+"/device"+"/"+ CommonData.sn, "recordings");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    long time = file.lastModified(); // 文件最后修改时间
                    String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(time));
                    audioList.add(new Audio(name,timeStr,path));
                }
            }
        }
        updateData();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        HhLog.e("audioList " + audioList);
        items.clear();
        if (audioList != null && audioList.size()!=0) {
            items.addAll(audioList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    public void startRecordTimes() {
        date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.SECOND,0);
        recordTimes.postValue(CommonUtil.parseZero(date.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(date.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(date.get(Calendar.SECOND)));
        runTimes();
    }
    private void runTimes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(recording){
                    date.add(Calendar.SECOND,1);
                    recordTimes.postValue(CommonUtil.parseZero(date.get(Calendar.HOUR_OF_DAY))+":"+CommonUtil.parseZero(date.get(Calendar.MINUTE))+":"+CommonUtil.parseZero(date.get(Calendar.SECOND)));
                    runTimes();
                }
            }
        },1000);
    }
    public void stopRecordTimes() {
        recording = false;
        recordTimes.postValue("点击开始录音");
    }

    public void uploadAudio() {
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        HhHttp.post()
                .url(URLConstant.UPLOAD_AUDIO()).addFile("file",  "record_" + System.currentTimeMillis(),new File(outputFilePath))
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: post UPLOAD_AUDIO " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {

                            } else {
                                Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
                                loading.setValue(new LoadingEvent(false));
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
    public void uploadAudio2() {
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        PostFormBuilder postFormBuilder = HhHttp.post()
                .url(URLConstant.UPLOAD_AUDIO());
        try{
            postFormBuilder.addFile("file",  "record_" + System.currentTimeMillis(),new File(outputFilePath));
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        postFormBuilder
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: post UPLOAD_AUDIO " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {

                            } else {
                                Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
                                loading.setValue(new LoadingEvent(false));
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
}
