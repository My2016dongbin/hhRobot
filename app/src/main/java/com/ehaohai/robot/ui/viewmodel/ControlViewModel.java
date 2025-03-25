package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;

public class ControlViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public boolean fanShen = false;
    public boolean shenLanYao = false;
    public boolean woShou = false;
    public boolean biXin = false;
    public boolean puRen = false;
    public boolean jump = false;
    public boolean zuNi = false;
    public boolean zhanLi = false;
    public boolean zuoXia = false;
    public boolean woDao = false;
    public boolean lock = false;
    public boolean baiZiShi = false;

    public boolean force = false;
    public boolean record = false;
    public boolean stop = false;
    public int stopDistance = 160;
    public boolean isDog = true;

    public void start(Context context) {
        this.context = context;
    }

}
