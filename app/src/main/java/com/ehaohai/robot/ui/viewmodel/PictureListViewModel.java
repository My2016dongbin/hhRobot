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
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
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


        HhHttp.get()
                .url(URLConstant.GET_FACE_LIST())
                .addParams("face_name",  "huang")
                //.addParams("uuid",  "")
                .addParams("pageNum",  "1")
                .addParams("pageSize",  "20")
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post GET_FACE_LIST " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {

                            } else {

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

/*        RequestParams params = new RequestParams(URLConstant.GET_FACE_LIST());
//        params.addBodyParameter("face_name","吴彦祖");
//        params.addBodyParameter("uuid","");
        params.addBodyParameter("pageNum","1");
        params.addBodyParameter("pageSize","20");
        HhHttp.getX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("onSuccess: post GET_FACE_LIST " + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e("onError: post GET_FACE_LIST " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });*/

        faceList.add(new Face("1","张三","",state,false));
        faceList.add(new Face("2","李四","",state,false));
        faceList.add(new Face("3","王五","",state,false));
        faceList.add(new Face("4","赵六","",state,false));
        faceList.add(new Face("5","张张","",state,false));
        faceList.add(new Face("6","李李","",state,false));
        faceList.add(new Face("7","宋宋","",state,false));
        faceList.add(new Face("8","王王","",state,false));
        faceList.add(new Face("9","京东","",state,false));
        updateDataFace();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataPic() {
        items.clear();
        if (pictureList != null && pictureList.size()!=0) {
            items.addAll(pictureList);
        }else{
            //items.add(new Empty());
            return;
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
            //items.add(new Empty());
            return;
        }

        assertAllRegistered(adapterFace, itemsFace);
        adapterFace.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSelected(Picture picture) {
        items.clear();
        if (pictureList != null && pictureList.size()!=0) {
            items.addAll(pictureList);
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
