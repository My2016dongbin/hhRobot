package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;

public class MessageSettingViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }
}
