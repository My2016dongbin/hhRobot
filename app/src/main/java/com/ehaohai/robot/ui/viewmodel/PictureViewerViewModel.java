package com.ehaohai.robot.ui.viewmodel;

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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class PictureViewerViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> message = new MutableLiveData<>();
    public int pictureIndex = 0;
    public List<FacePicture> facePictureList = new ArrayList<>();
    public boolean canDelete = false;
    public boolean online = false;
    public ArrayList<String> urls = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }


    public void deleteOnline(Action click) {
        String url = URLConstant.DELETE_FACE_PICTURE()+"/"+facePictureList.get(pictureIndex).getUuid()+"/"+facePictureList.get(pictureIndex).getImgName();
        HhLog.e("DELETE_FACE_PICTURE " + url);
        HhHttp.delete()
                .url(url+"?face_uuid="+facePictureList.get(pictureIndex).getUuid()+"&img_name="+facePictureList.get(pictureIndex).getImgName()+"&face_name="+facePictureList.get(pictureIndex).getFaceName())
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
}
