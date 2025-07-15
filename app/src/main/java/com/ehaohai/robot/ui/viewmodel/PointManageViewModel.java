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
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class PointManageViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<Point> pointList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
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

    public void editReplace(Point editPoint) {
        int index = -1;
        for (int i = 0; i < pointList.size(); i++) {
            Point pointModel = pointList.get(i);
            if(Objects.equals(pointModel.getId(), editPoint.getId())){
                index = i;
            }
        }
        if(index >= 0){
            pointList.remove(index);
            pointList.add(index,editPoint);

            updateData();

            ///点位修改提交
            postEditName();
        }
    }

    /**
     * 点位修改提交
     */
    public void postEditName(){
        loading.setValue(new LoadingEvent(true));
        List<List<String>> points = new ArrayList<>();
        ///处理数据
        for (int i = 0; i < pointList.size(); i++) {
            Point pointModel = pointList.get(i);
            List<String> model = new ArrayList<>();
            model.add(pointModel.getId());
            model.add(pointModel.getX());
            model.add(pointModel.getY());
            model.add(pointModel.getZ());
            model.add(pointModel.getYaw());
            model.add(pointModel.getA());
            model.add(pointModel.getB());
            model.add(pointModel.getC());
            model.add(pointModel.getD());
            model.add(pointModel.getFloor());
            model.add(pointModel.getName());
            points.add(model);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject param = new JSONObject();
        try {
            param.put("points",points);

            jsonObject.put("type","points");
            jsonObject.put("cmd","points_download");
            jsonObject.put("seq",new Random().nextInt(10000));
            jsonObject.put("param",new Gson().toJson(param));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "onSuccess: TASK_COMMAND up = " + URLConstant.TASK_COMMAND());
        Log.e("TAG", "onSuccess: TASK_COMMAND up = " + jsonObject);
        Log.e("TAG", "onSuccess: TASK_COMMAND up = " + CommonData.token);
        HhHttp.postString()
                .url(URLConstant.TASK_COMMAND())
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        Log.e("TAG", "onSuccess: TASK_COMMAND up = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();

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
