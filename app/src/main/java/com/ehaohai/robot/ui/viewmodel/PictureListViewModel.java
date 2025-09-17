package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
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
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class PictureListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<Picture> pictureList = new ArrayList<>();
    public List<Face> faceList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public MultiTypeAdapter adapterFace;
    public boolean isPic = true;
    public String facePath;
    public List<Object> items = new ArrayList<>();
    public List<Object> itemsFace = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    public void postPictureList(){
        pictureList = new ArrayList<>();

        List<String> robotPictureList = CommonUtil.getRobotPictureList(CommonData.sn);
        HhLog.e("robotPictureList " + robotPictureList);
        for (int i = 0; i < robotPictureList.size(); i++) {
            String name = robotPictureList.get(i);
            String path = CommonUtil.getRobotPicturePath(CommonData.sn) + "/" + name;
            pictureList.add(new Picture(i+"",name,path,state,false));
        }
        updateDataPic();
    }

    public void postFaceList(){
        faceList = new ArrayList<>();
        loading.setValue(new LoadingEvent(true));
        HhHttp.post()
                .url(URLConstant.GET_FACE_FILE_LIST())
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post GET_FACE_FILE_LIST " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            faceList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Face>>(){}.getType());

                            updateDataFace();

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

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataPic() {
        items.clear();
        if (pictureList != null && pictureList.size()!=0) {
            items.addAll(pictureList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataFace() {
        itemsFace.clear();
        if (faceList != null && faceList.size()!=0) {
            itemsFace.addAll(faceList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapterFace, itemsFace);
        adapterFace.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSelected(int index) {
        items.clear();
        if (pictureList != null && pictureList.size()!=0) {
            items.addAll(pictureList);
        }else{
            //items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        try{
            adapter.notifyItemChanged(index);
        }catch (Exception e){
            adapter.notifyDataSetChanged();
        }
    }

    public void createFaceFile(String name,String path) {
        loading.setValue(new LoadingEvent(true,"正在创建.."));
        HhHttp.post()
                .url(URLConstant.ADD_FACE_FILE())
                .addParams("face_name",  name)
                .addFile("image",  path,new File(path))
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post ADD_FACE_FILE " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new PictureRefresh());
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
}
