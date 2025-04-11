package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.model.Device;

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
        deviceList.add(new Device("1","浩海机器狗","来自共享","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg","1","Go2 EDU"));
        deviceList.add(new Device("2","轮式机器人","来自共享","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg","1","1888"));
        device.postValue(device.getValue()+"-");
    }

}
