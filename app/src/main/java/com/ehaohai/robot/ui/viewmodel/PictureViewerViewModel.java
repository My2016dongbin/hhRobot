package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;

import java.util.ArrayList;

public class PictureViewerViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> message = new MutableLiveData<>();
    public int pictureIndex = 0;
    public boolean canDelete = false;
    public boolean online = false;
    public ArrayList<String> urls = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }


}
