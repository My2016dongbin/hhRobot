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

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public String fileName;
    public boolean recording = false;
    public Calendar date;
    public final MutableLiveData<String> recordTimes = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void getAudioList() {
        audioList = new ArrayList<>();

        HhLog.e("onSuccess: post GET_AUDIO_LIST " + URLConstant.GET_AUDIO_LIST());
        HhHttp.post()
                .url(URLConstant.GET_AUDIO_LIST())
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("onSuccess: post GET_AUDIO_LIST " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray videos = jsonObject.getJSONArray("videos");
                            if (videos.length()>0) {
                                audioList = new Gson().fromJson(videos.toString(), new TypeToken<List<Audio>>(){}.getType());
                                updateData();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                    }
                });
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
        recordTimes.postValue(fileName);
    }

    public void uploadAudio() {
        loading.setValue(new LoadingEvent(true,"正在提交.."));
        HhLog.e("onSuccess: post UPLOAD_AUDIO " + URLConstant.UPLOAD_AUDIO());
        HhLog.e("onSuccess: post UPLOAD_AUDIO " + fileName);
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
                                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                                outputFilePath = "";
                                fileName = "";
                                recordTimes.postValue("点击开始录音");
                                getAudioList();
                            } else {
                                Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
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

    public void deleteAudio(String audioName) {
        loading.setValue(new LoadingEvent(true,"正在删除.."));
        RequestParams params = new RequestParams(URLConstant.DELETE_AUDIO());
        params.addBodyParameter("filename" , audioName);
        HhLog.e("onSuccess: post DELETE_AUDIO " + URLConstant.DELETE_AUDIO());
        HhLog.e("onSuccess: post DELETE_AUDIO " + CommonData.token);
        HhLog.e("onSuccess: post DELETE_AUDIO " + params);
        HhLog.e("onSuccess: post DELETE_AUDIO " + audioName);
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post DELETE_AUDIO " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                getAudioList();
                            } else {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e, boolean isOnCallback) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false));
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }
    public void renameAudio(String oldName,String newName) {
        loading.setValue(new LoadingEvent(true,"正在修改.."));
        RequestParams params = new RequestParams(URLConstant.RENAME_AUDIO());
        params.addBodyParameter("old_filename" , oldName);
        params.addBodyParameter("new_filename" , newName);
        HhLog.e("onSuccess: post RENAME_AUDIO " + URLConstant.RENAME_AUDIO());
        HhLog.e("onSuccess: post RENAME_AUDIO " + CommonData.token);
        HhLog.e("onSuccess: post RENAME_AUDIO " + params);
        HhLog.e("onSuccess: post RENAME_AUDIO " + oldName + " to " + newName);
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String response) {
                loading.setValue(new LoadingEvent(false));
                HhLog.e("onSuccess: post RENAME_AUDIO " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        getAudioList();
                    } else {
                        Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                HhLog.e("onFailure: " + e.toString());
                msg.setValue(e.getMessage());
                loading.setValue(new LoadingEvent(false));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
