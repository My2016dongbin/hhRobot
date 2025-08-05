package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.amap.api.maps.AMap;
import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.base.LoggedInStringCallback;
import com.ehaohai.robot.constant.HhHttp;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.event.LoadingEvent;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.Call;

public class MapLocationViewModel extends BaseViewModel {
    public Context context;
    public AMap aMap;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }
}
