package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Face;
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class PointListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<Point> pointList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }


    public void postListLocal(){
        pointList = new ArrayList<>();

        pointList.add(new Point("1","","","","","","","","","1","张三"));
        pointList.add(new Point("2","","","","","","","","","1","李四"));
        pointList.add(new Point("3","","","","","","","","","1","王五"));
        pointList.add(new Point("4","","","","","","","","","1","赵六"));
        pointList.add(new Point("5","","","","","","","","","1","张张"));
        pointList.add(new Point("6","","","","","","","","","1","李李"));
        pointList.add(new Point("7","","","","","","","","","1","宋宋"));
        pointList.add(new Point("8","","","","","","","","","1","王王"));
        pointList.add(new Point("9","","","","","","","","","1","京东"));
        updateData();
    }


    public void postList(){
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
                            JSONObject result = jsonObject.getJSONObject("param");
                            JSONObject points_json = result.getJSONObject("points_json");
                            JSONArray points = points_json.getJSONArray("points");
                            for (int i = 0; i < points.length(); i++) {
                                JSONArray point = (JSONArray) points.get(i);
                                pointList.add(new Point(point.get(0)+"",point.get(1)+"",point.get(2)+"",point.get(3)+"",point.get(4)+"",point.get(5)+"",point.get(6)+"",point.get(7)+"",point.get(8)+"",point.get(9)+"",point.get(10)+""));
                            }
                            updateData();

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
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            items.addAll(pointList);
        }else{
            //items.add(new Empty());
            return;
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSelected(Picture picture) {
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            items.addAll(pointList);
        }else{
            //items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        try{
            adapter.notifyItemChanged(Integer.parseInt(picture.getId()));
        }catch (Exception e){
            adapter.notifyDataSetChanged();
        }
    }
}
