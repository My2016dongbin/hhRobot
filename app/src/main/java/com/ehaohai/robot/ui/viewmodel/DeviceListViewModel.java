package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.model.Device;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> device = new MutableLiveData<>();

    public List<Device> deviceList = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    public void postDeviceList(){
        deviceList = new ArrayList<>();
        List<String> robotFileList = CommonUtil.getRobotFileList();
        for (int i = 0; i < robotFileList.size(); i++) {
            String robotSn = robotFileList.get(i);
            JSONObject jsonObject = CommonUtil.getRobotFileConfigJson(robotSn);
            try {
                deviceList.add(new Device(robotSn,jsonObject.getString("name"),jsonObject.getString("ip"),jsonObject.getJSONObject("config")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        device.postValue(device.getValue()+"-");
    }

}
