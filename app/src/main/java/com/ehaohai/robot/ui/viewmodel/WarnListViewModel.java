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
    public final MutableLiveData<String> warn = new MutableLiveData<>();
    public final MutableLiveData<String> todayCount = new MutableLiveData<>();
    public final MutableLiveData<String> allCount = new MutableLiveData<>();
    public MultiTypeAdapter aiAdapter;
    public MultiTypeAdapter bugAdapter;
    public List<Object> aiItems = new ArrayList<>();
    public List<Object> bugItems = new ArrayList<>();
    public List<Warn> warnList = new ArrayList<>();
    public boolean isAi = true;
    public int pageNum = 1;

    public void start(Context context) {
        this.context = context;

    }

    public void postWarnList(){
        HhHttp.get()
                .url(URLConstant.ALARM_LIST())
//                .addParams("alarm_type","")
//                .addParams("start_time","")
//                .addParams("end_time","")
                .addParams("pageNum",pageNum+"")
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

//    public void postWarnList(){
//        warnList = new ArrayList<>();
//        warnList.add(new Warn(0,"0","2024.09.28","机器狗","浩海机器狗","烟雾火焰","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",true));
//        warnList.add(new Warn(1,"1","2024.09.28","机器狗","浩海机器狗","灯光状态","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",true));
//        warnList.add(new Warn(2,"2","2024.09.28","机器狗","浩海机器狗","灯光状态","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
//        warnList.add(new Warn(3,"3","2024.09.28","机器狗","浩海机器狗","人员身份","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
//        warnList.add(new Warn(4,"4","2024.09.28","机器狗","浩海机器狗","抽烟识别","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
//        warnList.add(new Warn(5,"5","2024.09.28","机器狗","浩海机器狗","烟雾火焰","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
//        updateDataAi();
//        updateDataBug();
//    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataAi() {
        if(pageNum==1){
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

    public void updateDataBug() {
        bugItems.clear();
        if (warnList != null && warnList.size()!=0) {
            bugItems.addAll(warnList);
        }else{
            bugItems.add(new Empty());
        }

        assertAllRegistered(aiAdapter, bugItems);
        bugAdapter.notifyDataSetChanged();
    }

}
