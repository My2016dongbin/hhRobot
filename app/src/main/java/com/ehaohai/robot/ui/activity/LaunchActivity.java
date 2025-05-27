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
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_MULTICAST_STATE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        Object login = SPUtils.get(HhApplication.getInstance(), SPValue.login, false);
                        if(login!=null && (boolean)login){
                            CommonData.token = (String) SPUtils.get(HhApplication.getInstance(), SPValue.token, "");
                            URLConstant.setLocalPath((String) SPUtils.get(HhApplication.getInstance(), SPValue.offlineIp, ""));
                            String userName = (String) SPUtils.get(HhApplication.getInstance(), SPValue.userName, "");
                            String password = (String) SPUtils.get(HhApplication.getInstance(), SPValue.password, "");

//                            obtainViewModel().login(userName,password);
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                finish();
//                                playExitAnimation(1);
                            },2000);
                        }else{
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                                finish();
//                                playExitAnimation(0);
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
    private void playExitAnimation(int home) {
        Bitmap splashBitmap = getBitmapFromView(binding.splashContainer);
        if (splashBitmap == null) {
            if (home == 1) {
                goHome();
            } else {
                goLogin();
            }
            return;
        }

        int width = splashBitmap.getWidth();
        int height = splashBitmap.getHeight();

        Bitmap leftBitmap = Bitmap.createBitmap(splashBitmap, 0, 0, width / 2, height);
        Bitmap rightBitmap = Bitmap.createBitmap(splashBitmap, width / 2, 0, width / 2, height);

        ImageView leftView = new ImageView(this);
        ImageView rightView = new ImageView(this);
        leftView.setImageBitmap(leftBitmap);
        rightView.setImageBitmap(rightBitmap);

        FrameLayout root = (FrameLayout) binding.splashContainer.getParent();
        root.addView(leftView);
        root.addView(rightView);

        binding.splashContainer.setVisibility(View.GONE);

        FrameLayout.LayoutParams leftParams = new FrameLayout.LayoutParams(width / 2, height);
        FrameLayout.LayoutParams rightParams = new FrameLayout.LayoutParams(width / 2, height);
        leftParams.leftMargin = 0;
        rightParams.leftMargin = width / 2;
        leftView.setLayoutParams(leftParams);
        rightView.setLayoutParams(rightParams);

        // 将主页或登录页先设置为透明（假设是 activity_root 背后的 View）
        View rootView = root.getChildAt(0); // 假设主页内容在第0个
        rootView.setAlpha(0f);
        rootView.setVisibility(View.VISIBLE); // 显示主页内容

        // 向左右滑动 + 淡出
        leftView.animate()
                .translationXBy(-width / 2f)
                .alpha(0f)
                .setDuration(600)
                .start();

        rightView.animate()
                .translationXBy(width / 2f)
                .alpha(0f)
                .setDuration(600)
                .start();

        // 背景页面逐渐显现
        rootView.animate()
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    if (home == 1) {
                        goHome();
                    } else {
                        goLogin();
                    }
                })
                .start();
    }

    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0); // 去除默认转场
        finish();
    }
    private void goLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(0, 0); // 去除默认转场
        finish();
    }

    private Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
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