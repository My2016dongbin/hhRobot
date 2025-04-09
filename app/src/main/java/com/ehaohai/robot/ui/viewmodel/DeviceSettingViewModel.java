package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.model.UdpMessage;

import java.util.ArrayList;
import java.util.List;

public class DeviceSettingViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> message = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

}
