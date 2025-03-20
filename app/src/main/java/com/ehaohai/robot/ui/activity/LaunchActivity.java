package com.ehaohai.robot.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityLaunchBinding;
import com.ehaohai.robot.ui.viewmodel.LaunchViewModel;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LaunchActivity extends BaseLiveActivity<ActivityLaunchBinding, LaunchViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions();
    }

    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_MULTICAST_STATE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
                        CommonData.hasMainApp = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainApp, false);
                        CommonData.hasMainVideo = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainVideo, false);
                        CommonData.hasMainMessage = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMessage, false);
                        CommonData.hasMainMap = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMap, false);
                        CommonData.hasMainMy = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.hasMainMy, true);//强制显示'我的'菜单
                        if(login!=null && (boolean)login){
                            CommonData.token = (String) SPUtils.get(HhApplication.getInstance(), SPValue.token, "");
                            String userName = (String) SPUtils.get(HhApplication.getInstance(), SPValue.userName, "");
                            String password = (String) SPUtils.get(HhApplication.getInstance(), SPValue.password, "");

                            obtainViewModel().login(userName,password);
                        }else{
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                                finish();
                            },2000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected ActivityLaunchBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_launch);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LaunchViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LaunchViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}