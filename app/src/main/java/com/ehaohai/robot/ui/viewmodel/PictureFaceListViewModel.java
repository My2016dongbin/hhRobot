package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.multitype.FacePicture;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class PictureFaceListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<FacePicture> pictureList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public MultiTypeAdapter adapterFace;
    public String facePath;
    public String fileName = "";
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    public void postFaceList(){
        pictureList = new ArrayList<>();
        loading.setValue(new LoadingEvent(true));
        HhHttp.get()
                .url(URLConstant.GET_FACE_LIST())
                .addParams("face_name",  fileName)
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post GET_FACE_LIST " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray items = data.getJSONArray("items");
                            pictureList = new Gson().fromJson(items.toString(), new TypeToken<List<FacePicture>>(){}.getType());

                            updateDataPic();

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
            //items.add(new Empty());
            return;
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
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

    public void deleteFile(Action click) {
        loading.setValue(new LoadingEvent(true,"正在删除.."));
        String url = URLConstant.DELETE_FACE_FILE()+"/"+pictureList.get(0).getUuid();
        HhLog.e("DELETE_FACE_PICTURE " + url);
        HhHttp.delete()
                .url(url+"?face_uuid="+pictureList.get(0).getUuid())
                .build()
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        HhLog.e("onSuccess: post DELETE_FACE_FILE " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new PictureRefresh());
                                click.click();
                            } else {
                                //Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
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

    public void deletePicture(List<FacePicture> list,Action action) {
        loading.setValue(new LoadingEvent(true,"正在删除.."));
        final int[] size = {0};
        for (int i = 0; i < list.size(); i++) {
            FacePicture facePicture = list.get(i);
            String url = URLConstant.DELETE_FACE_PICTURE()+"/"+facePicture.getUuid()+"/"+facePicture.getImgName();
            HhLog.e("DELETE_FACE_PICTURE " + url);
            HhHttp.delete()
                    .url(url+"?face_uuid="+facePicture.getUuid()+"&img_name="+facePicture.getImgName()+"&face_name="+facePicture.getFaceName())
                    .build()
                    .execute(new LoggedInStringCallback(this,context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            loading.setValue(new LoadingEvent(false));
                            HhLog.e("onSuccess: post DELETE_FACE_PICTURE " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String code = jsonObject.getString("code");
                                if (code.equals("200")) {
                                    size[0]++;
                                    if(size[0]>=list.size()){
                                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PictureRefresh());
                                        action.click();
                                    }
                                } else {
                                    //Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
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
}
