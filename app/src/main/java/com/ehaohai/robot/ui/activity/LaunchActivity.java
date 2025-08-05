package com.ehaohai.robot.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.databinding.ActivityLaunchBinding;
import com.ehaohai.robot.ui.viewmodel.LaunchViewModel;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LaunchActivity extends BaseLiveActivity<ActivityLaunchBinding, LaunchViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreen(this);
        permissions();
    }

    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.INTERNET/*,Manifest.permission.RECORD_AUDIO*/,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_MULTICAST_STATE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
                        if(login!=null && (boolean)login){
                            CommonData.token = (String) SPUtils.get(HhApplication.getInstance(), SPValue.token, "");
                            CommonData.sn = (String) SPUtils.get(HhApplication.getInstance(), SPValue.sn, "");
                            String offlineIp = (String)SPUtils.get(HhApplication.getInstance(), SPValue.offlineIp, "");
                            HhLog.e("offlineIp " + offlineIp);
                            ///防止空Ip异常
                            if(offlineIp==null || offlineIp.isEmpty()){
                                Toast.makeText(LaunchActivity.this, "登录信息失效", Toast.LENGTH_SHORT).show();
                                CommonUtil.accountClear();
                                finish();
                                return;
                            }
                            URLConstant.setLocalPath(offlineIp);
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                finish();
                            },2000);
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