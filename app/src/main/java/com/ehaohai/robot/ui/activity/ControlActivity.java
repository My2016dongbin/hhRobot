package com.ehaohai.robot.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.databinding.ActivityControlBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.model.Heart;
import com.ehaohai.robot.ui.service.ScreenRecordService;
import com.ehaohai.robot.ui.viewmodel.ControlViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.ActionDownUp;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.NetworkSpeedMonitor;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ControlActivity extends BaseLiveActivity<ActivityControlBinding, ControlViewModel> {
    BatteryReceiver batteryReceiver;
    private LibVLC libVLCDog;
    private MediaPlayer mediaPlayerDog;
    private Media mediaDog;
    private IVLCVout ivlcVoutDog;
    private LibVLC libVLCLight;
    private MediaPlayer mediaPlayerLight;
    private Media mediaLight;
    private IVLCVout ivlcVoutLight;
    private LibVLC libVLCHot;
    private MediaPlayer mediaPlayerHot;
    private Media mediaHot;
    private IVLCVout ivlcVoutHot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///设备心跳数据
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Heart heart) {
        binding.horizontalBattery.setPower(heart.getBatteryPercentage());
        binding.speed.setText(heart.getLinearSpeed() + "m/s");
        binding.temperature2.setText(heart.getAmbTemperature() + "°C");
        binding.temperature.setText(heart.getAmbTemperature() + "°C");
        binding.humidity.setText(heart.getAmbHumidity() + "%RH");
        binding.smoke.setText(heart.getSomkeValue() + "PPM");
    }

    private void init_() {
        //电池电量变化监控
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, filter);

        //网速变化监控
        NetworkSpeedMonitor monitor = new NetworkSpeedMonitor(new NetworkSpeedMonitor.SpeedListener() {
            @Override
            public void onSpeedUpdate(long downloadSpeed, long uploadSpeed) {
                binding.textWifi.setText(CommonUtil.parseNetSpeed(downloadSpeed));
            }
        });
        monitor.startMonitoring();

        //隐藏急停按钮（右侧隐藏一半）
        binding.stop.setTranslationX(obtainViewModel().stopDistance); // 向左偏移 100px，使右侧隐藏

        initLeftControl();
        initRightControl();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlayerDog();
            }
        }, 200);
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.back, new Action() {
            @Override
            public void click() {
                if(obtainViewModel().record){
                    Toast.makeText(ControlActivity.this, "录制中，不允许退出操作页面，请先关闭", Toast.LENGTH_SHORT).show();
                }else{
                    finish();
                }
            }
        });
        CommonUtil.click(binding.dataX, new Action() {
            @Override
            public void click() {
                obtainViewModel().data.postValue(false);
            }
        });
        CommonUtil.click(binding.mapMode, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(ControlActivity.this, MapModeActivity.class));
            }
        });
        CommonUtil.click(binding.pointMode, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(ControlActivity.this, PointModeActivity.class));
            }
        });
        binding.switchData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                obtainViewModel().data.postValue(isChecked);
            }
        });
        binding.switchPan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                obtainViewModel().pan.postValue(isChecked);
            }
        });
        binding.switchForce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                obtainViewModel().force.postValue(isChecked);
                if (isChecked) {
                    CommonUtil.applyFancyAnimation(binding.force);
                    obtainViewModel().sportControl("manual", "obstacle", "ON");
                } else {
                    CommonUtil.applyFancyBackAnimation(binding.force);
                    obtainViewModel().sportControl("manual", "obstacle", "OFF");
                }
            }
        });
        binding.lightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                obtainViewModel().sportControl("manual", "light", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        CommonUtil.clickDownUp(binding.fixLess, new ActionDownUp() {
            @Override
            public void clickDown() {
                zoomParam = "-1";
                zoomDown = true;
                zoomRunner();
            }

            @Override
            public void clickUp() {
                zoomDown = false;
            }
        });
        CommonUtil.clickDownUp(binding.fixMore, new ActionDownUp() {
            @Override
            public void clickDown() {
                zoomParam = "1";
                zoomDown = true;
                zoomRunner();
            }

            @Override
            public void clickUp() {
                zoomDown = false;
            }
        });
        //播放器
        binding.llPlayer.setOnClickListener(view -> {
            //startActivity(new Intent(this,AudioLocalListActivity.class));
            startActivity(new Intent(this, AudioListActivity.class));
        });
        //设置
        binding.setting.setOnClickListener(view -> {
            binding.settingLayout.openDrawer(GravityCompat.END);
        });
        //十字准星
        binding.switchCenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.center.setVisibility(View.VISIBLE);
                } else {
                    binding.center.setVisibility(View.GONE);
                }
            }
        });
        ///急停
        binding.stop.setOnClickListener(view -> {
            stopAnimation();
            if (obtainViewModel().stop) {
                obtainViewModel().sportControl("manual", "es", "");
                hideOtherButton(view);
                hideOtherButtonStatus(view);
            } else {
                obtainViewModel().sportControl("manual", "recover", "");
            }
        });
        ///报警
        CommonUtil.click(binding.warn, () -> {
            startActivity(new Intent(ControlActivity.this, SingleWarnListActivity.class));
        });
        ///对讲
        binding.speak.setOnClickListener(view -> {
            obtainViewModel().speak = !obtainViewModel().speak;
            if (obtainViewModel().speak) {
                permission();
                CommonUtil.applyFancyAnimation(view);
                voiceAnimation();
            } else {
                CommonUtil.applyFancyBackAnimation(view);
                binding.flVoice.setVisibility(View.GONE);
                binding.llVoice.setVisibility(View.GONE);
            }
        });
        binding.voiceX.setOnClickListener(view -> {
            voiceAnimationBack();
            obtainViewModel().speak = false;
            CommonUtil.applyFancyBackAnimation(binding.speak);
        });
        binding.flVoice.setOnClickListener(view -> {
            voiceAnimationBack();
            obtainViewModel().speak = false;
            CommonUtil.applyFancyBackAnimation(binding.speak);
        });
        binding.voiceStart.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    binding.voiceStart.setBackgroundResource(R.drawable.circle_voice_big_selected);
                    binding.voiceCount.setVisibility(View.VISIBLE);
                    obtainViewModel().voice = true;
                    obtainViewModel().startRecordTimesVoice();
                    startRecordVoice();
                    break;

                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:
                    binding.voiceStart.setBackgroundResource(R.drawable.circle_voice_big);
                    binding.voiceCount.setVisibility(View.GONE);
                    obtainViewModel().voice = false;
                    obtainViewModel().stopVoiceTimes();
                    stopRecordVoice();
                    break;
            }
            return true;
        });
        ///机器狗
        binding.dog.setOnClickListener(view -> {
            obtainViewModel().cloudSet = false;
            if (!obtainViewModel().isDog) {
                obtainViewModel().isDog = true;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.cloud);

                binding.warn.setVisibility(View.VISIBLE);
                binding.speak.setVisibility(View.VISIBLE);
                binding.force.setVisibility(View.VISIBLE);
                binding.notice.setVisibility(View.VISIBLE);
                binding.stop.setVisibility(View.VISIBLE);
                binding.controlLeft.setVisibility(View.VISIBLE);
                binding.controlRight.setVisibility(View.VISIBLE);
                binding.llDogButton.setVisibility(View.VISIBLE);

                binding.cloudSet.setVisibility(View.GONE);
                binding.controlCloud.setVisibility(View.GONE);
                binding.flCloudSet.setVisibility(View.GONE);

                binding.llCloud.setVisibility(View.GONE);
                binding.dogLive.setVisibility(View.VISIBLE);

                binding.pointMode.setVisibility(View.VISIBLE);
                binding.mapMode.setVisibility(View.VISIBLE);

                initLeftControl();
                initRightControl();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPlayerDog();
                        releasePlayerLight();
                        releasePlayerHot();
                    }
                }, 500);
            }
        });
        ///云台
        binding.cloud.setOnClickListener(view -> {
            if (obtainViewModel().isDog) {
                obtainViewModel().isDog = false;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.dog);

                binding.cloudSet.setVisibility(View.VISIBLE);
                binding.controlCloud.setVisibility(View.VISIBLE);

                binding.warn.setVisibility(View.GONE);
                binding.speak.setVisibility(View.GONE);
                binding.force.setVisibility(View.GONE);
                binding.notice.setVisibility(View.GONE);
                binding.stop.setVisibility(View.GONE);
                binding.controlLeft.setVisibility(View.GONE);
                binding.controlRight.setVisibility(View.GONE);
                binding.llDogButton.setVisibility(View.GONE);

                binding.llCloud.setVisibility(View.VISIBLE);
                binding.dogLive.setVisibility(View.GONE);

                binding.pointMode.setVisibility(View.GONE);
                binding.mapMode.setVisibility(View.GONE);

                initCloudControl();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPlayerLight();
                        startPlayerHot();
                        releasePlayerDog();
                    }
                }, 500);
            }
        });
        ///(云台)设置
        CommonUtil.click(binding.cloudSet, () -> {
            obtainViewModel().cloudSet = !obtainViewModel().cloudSet;
            if (obtainViewModel().cloudSet) {
                binding.flCloudSet.setVisibility(View.VISIBLE);
                binding.cloudSetImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_set_blue));
                binding.cloudSetText.setTextColor(getResources().getColor(R.color.theme_color_blue));
            } else {
                binding.flCloudSet.setVisibility(View.GONE);
                binding.cloudSetImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_set_white));
                binding.cloudSetText.setTextColor(getResources().getColor(R.color.gray1));
            }
        });
        ///云台-设置菜单-关闭按钮
        /*binding.cloudSetX.setOnClickListener(view -> {
            binding.flCloudSet.setVisibility(View.GONE);
        });*/
        ///避障
        binding.force.setOnClickListener(view -> {
            Boolean value = obtainViewModel().force.getValue();
            obtainViewModel().force.postValue(!value);
            if (!value) {
                CommonUtil.applyFancyAnimation(view);
                obtainViewModel().sportControl("manual", "obstacle", "ON");
            } else {
                CommonUtil.applyFancyBackAnimation(view);
                obtainViewModel().sportControl("manual", "obstacle", "OFF");
            }
        });
        ///通知
        CommonUtil.click(binding.notice, () -> {
//            Toast.makeText(ControlActivity.this, "通知", Toast.LENGTH_SHORT).show();
        });
        ///截图
        CommonUtil.click(binding.screenshoot, () -> {
            if (obtainViewModel().isDog) {
                CommonUtil.captureSurfaceView(binding.dogLive, this);
            } else {
                //CommonUtil.captureSurfaceView(binding.cloudLightLive,this);
                //CommonUtil.captureSurfaceView(binding.cloudHotLive,this);
                CommonUtil.captureAndCombineSurfaceViews(binding.cloudLightLive, binding.cloudHotLive, this);
            }
        });
        ///录像
        CommonUtil.click(binding.recordImage, () -> {
            obtainViewModel().record = !obtainViewModel().record;
            if (obtainViewModel().record) {
                /*binding.videoCount.setVisibility(View.VISIBLE);
                binding.recordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                //开始计时并录制
                Toast.makeText(ControlActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
                obtainViewModel().startRecordTimes();*/
                requestScreenCapture();
            } else {
                try {
                    binding.videoCount.setVisibility(View.GONE);
                    binding.recordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_image));
                    //关闭计时并保存录像
                    obtainViewModel().stopRecordTimes();
                    ScreenRecordService.stopRecording(this);
                } catch (Exception e) {
                    //
                }
            }
        });
        ///伸懒腰
        binding.shenLanYao.setOnClickListener(view -> {
            obtainViewModel().shenLanYao = true;
            obtainViewModel().sportControl("manual", "sport", "Stretch");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///比心
        binding.biXin.setOnClickListener(view -> {
            obtainViewModel().biXin = true;
            obtainViewModel().sportControl("manual", "sport", "content");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///扑人
        binding.puRen.setOnClickListener(view -> {
            obtainViewModel().puRen = true;
            obtainViewModel().sportControl("manual", "sport", "FrontPounce");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///跳
        binding.qianTiao.setOnClickListener(view -> {
            obtainViewModel().jump = true;
            obtainViewModel().sportControl("manual", "sport", "FrontJump");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///舞蹈1
        binding.wuDao1.setOnClickListener(view -> {
            obtainViewModel().wuDao1 = true;
            obtainViewModel().sportControl("manual", "sport", "Dance1");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///舞蹈2
        binding.wuDao2.setOnClickListener(view -> {
            obtainViewModel().wuDao2 = true;
            obtainViewModel().sportControl("manual", "sport", "Dance2");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///灭火器
        binding.fire.setOnClickListener(view -> {
            obtainViewModel().fire = true;
            obtainViewModel().sportControl("manual", "sport", "Fire");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });

        ///阻尼
        binding.zuNi.setOnClickListener(view -> {
            obtainViewModel().zuNi = true;
            obtainViewModel().sportControl("manual", "sport", "Damp");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///站立
        binding.zhanLi.setOnClickListener(view -> {
            obtainViewModel().zhanLi = true;
            obtainViewModel().sportControl("manual", "sport", "BalanceStand");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///坐下
        binding.zuoXia.setOnClickListener(view -> {
            obtainViewModel().zuoXia = true;
            obtainViewModel().sportControl("manual", "sport", "Sit");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///卧倒
        binding.woDao.setOnClickListener(view -> {
            obtainViewModel().woDao = true;
            obtainViewModel().sportControl("manual", "sport", "StandDown");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///停止动作
        binding.lock.setOnClickListener(view -> {
            obtainViewModel().lock = true;
            obtainViewModel().sportControl("manual", "sport", "StopMove");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///打招呼
        binding.daZhaoHu.setOnClickListener(view -> {
            obtainViewModel().daZhaoHu = true;
            obtainViewModel().sportControl("manual", "sport", "Hello");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
    }

    private void permission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean b) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean zoomDown = false;
    private String zoomParam = "";

    private void zoomRunner() {
        if (zoomDown) {
            obtainViewModel().sportControl("manual", "CameraZoom", zoomParam);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    zoomRunner();
                }
            }, 300);
        }
    }

    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1000;

    private void requestScreenCapture() {
        MediaProjectionManager projectionManager =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        Intent captureIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            captureIntent = projectionManager.createScreenCaptureIntent();
        }
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                Intent serviceIntent = new Intent(this, ScreenRecordService.class);
                serviceIntent.putExtra(ScreenRecordService.RESULT_CODE, resultCode);
                serviceIntent.putExtra(ScreenRecordService.RESULT_DATA, data);
                //ContextCompat.startForegroundService(this, serviceIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }

                binding.videoCount.setVisibility(View.VISIBLE);
                binding.recordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_image_stop));
                //开始计时并录制
                Toast.makeText(ControlActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
                obtainViewModel().startRecordTimes();
            } else {
                Toast.makeText(this, "未授权录屏", Toast.LENGTH_SHORT).show();
                obtainViewModel().record = false;
            }
        }
    }


    private void voiceAnimation() {
        binding.flVoice.setVisibility(View.VISIBLE);
        binding.llVoice.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.llVoice, "translationY", 2000, 0);
        animator.setDuration(500); // Set duration of the animation
        animator.start();
    }

    private void voiceAnimationBack() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.llVoice, "translationY", 0, 2000);
        animator.setDuration(300); // Set duration of the animation
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.flVoice.setVisibility(View.GONE);
                binding.llVoice.setVisibility(View.GONE);
            }
        }, 200);
    }

    private void hideOtherButton(View view) {
        if (obtainViewModel().shenLanYao && view.getId() != binding.shenLanYao.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.shenLanYao);
            obtainViewModel().shenLanYao = false;
        }
        if (obtainViewModel().biXin && view.getId() != binding.biXin.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.biXin);
            obtainViewModel().biXin = false;
        }
        if (obtainViewModel().puRen && view.getId() != binding.puRen.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.puRen);
            obtainViewModel().puRen = false;
        }
        if (obtainViewModel().jump && view.getId() != binding.qianTiao.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.qianTiao);
            obtainViewModel().jump = false;
        }
        if (obtainViewModel().wuDao1 && view.getId() != binding.wuDao1.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.wuDao1);
            obtainViewModel().wuDao1 = false;
        }
        if (obtainViewModel().wuDao2 && view.getId() != binding.wuDao2.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.wuDao2);
            obtainViewModel().wuDao2 = false;
        }
        if (obtainViewModel().fire && view.getId() != binding.fire.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.fire);
            obtainViewModel().fire = false;
        }
    }

    private void hideOtherButtonStatus(View view) {
        if (obtainViewModel().zuNi && view.getId() != binding.zuNi.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.zuNi);
            obtainViewModel().zuNi = false;
        }
        if (obtainViewModel().zhanLi && view.getId() != binding.zhanLi.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.zhanLi);
            obtainViewModel().zhanLi = false;
        }
        if (obtainViewModel().zuoXia && view.getId() != binding.zuoXia.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.zuoXia);
            obtainViewModel().zuoXia = false;
        }
        if (obtainViewModel().woDao && view.getId() != binding.woDao.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.woDao);
            obtainViewModel().woDao = false;
        }
        if (obtainViewModel().lock && view.getId() != binding.lock.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.lock);
            obtainViewModel().lock = false;
        }
        if (obtainViewModel().daZhaoHu && view.getId() != binding.daZhaoHu.getId()) {
            CommonUtil.applyFancyBackAnimation(binding.daZhaoHu);
            obtainViewModel().daZhaoHu = false;
        }
    }

    @Override
    protected ActivityControlBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_control);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ControlViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ControlViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().recordTimes.observe(this, this::recordTimesChanged);
        obtainViewModel().voiceTimes.observe(this, this::voiceTimesChanged);
        obtainViewModel().pan.observe(this, this::panChanged);
        obtainViewModel().data.observe(this, this::dataChanged);
    }

    private void recordTimesChanged(String recordTimes) {
        binding.videoCount.setText(recordTimes);
    }

    private void voiceTimesChanged(String recordTimes) {
        binding.voiceCount.setText(recordTimes);
    }

    private void panChanged(boolean pan) {
        binding.pan.setVisibility(pan ? View.VISIBLE : View.GONE);
    }

    private void dataChanged(boolean data) {
        binding.data.setVisibility(data ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // 在活动销毁时取消广播接收器的注册
        unregisterReceiver(batteryReceiver);
    }

    public class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取电池的电量信息
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1); // 电池电量（0-100）
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1); // 电池的总容量

            // 计算电池的当前电量百分比
            float batteryPercentage = level / (float) scale * 100;

            // 获取电池状态（充电/放电等）
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String statusString = "Unknown";
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                statusString = "Charging";
            } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                statusString = "Discharging";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                statusString = "Full";
            }

            // 获取电池是否正在充电
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;

            // 获取电池的充电方式（USB, AC, 无线）
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            String chargePlugString = "Unknown";
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                chargePlugString = "USB";
            } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                chargePlugString = "AC";
            } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                chargePlugString = "Wireless";
            }

            // 显示电池状态信息
            String message = "Battery level: " + batteryPercentage + "%\n" +
                    "Status: " + statusString + "\n" +
                    "Charging: " + isCharging + "\n" +
                    "Charge Plug: " + chargePlugString;
            HhLog.e("battery " + message);
            //binding.horizontalBattery.setPower(CommonUtil.parseInt(batteryPercentage+""));//暂时隐藏手机电量
        }
    }


    //急停切换动画（展开/收起）
    private void stopAnimation() {
        float targetTranslationX = obtainViewModel().stop ? obtainViewModel().stopDistance : 0;
        obtainViewModel().stop = !obtainViewModel().stop;

        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.stop, "translationX", targetTranslationX);
        animator.setDuration(500); // 动画时间 500ms
        animator.start();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initLeftControl() {
        // 在 View 渲染完毕后获取中心点坐标
        binding.controlLeft.post(() -> {
            centerX = binding.controlLeft.getX() + binding.controlLeft.getWidth() / 2f;
            centerY = binding.controlLeft.getY() + binding.controlLeft.getHeight() / 2f;
            baseX = binding.controlLeft.getX();
            baseY = binding.controlLeft.getY();
            maxRadius = binding.controlLeft.getWidth() * 0.2f; // 限制最大移动范围（摇杆半径 * 1.2）
        });

        binding.controlLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float touchX = event.getRawX();
                float touchY = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDragging = true;
                        startControlRunner();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            updateJoystickPosition(touchX, touchY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        obtainViewModel().vxPost = 0;
                        obtainViewModel().vyPost = 0;
                        resetJoystickPosition();
                        break;
                }
                return true;
            }
        });
    }

    private void startControlRunner() {
        if (isDragging || isDraggingRight) {
            obtainViewModel().controlPost();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startControlRunner();
                }
            }, 200);
        }
    }

    private void startControlRunnerCloud() {
        if (isDraggingCloud) {
            obtainViewModel().controlPostCloud();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startControlRunnerCloud();
                }
            }, 200);
        }
    }

    private float centerX, centerY, baseX, baseY;
    private boolean isDragging = false;
    private float maxRadius; // 限制滑动范围

    // 更新摇杆位置，并计算角度
    private void updateJoystickPosition(float touchX, float touchY) {
        float deltaX = touchX - centerX;
        float deltaY = touchY - centerY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX)); // 计算角度

        // 限制摇杆滑动范围
        if (distance > maxRadius) {
            float scale = (float) (maxRadius / distance);
            deltaX *= scale;
            deltaY *= scale;
        }

        binding.controlLeft.setX(baseX + deltaX);
        binding.controlLeft.setY(baseY + deltaY);

        // 角度转换到 0°~360°（右侧 0°，顺时针）
        if (angle < 0) {
            angle += 360;
        }

        onJoystickMove(angle); // 监听角度
    }

    // 摇杆回到初始位置（带动画）
    private void resetJoystickPosition() {
        ObjectAnimator animX = ObjectAnimator.ofFloat(binding.controlLeft, "x", baseX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(binding.controlLeft, "y", baseY);
        animX.setDuration(300);
        animY.setDuration(300);
        animX.start();
        animY.start();
        obtainViewModel().angleLeft = 999;
    }

    // 监听摇杆的角度
    private void onJoystickMove(double angle) {
        obtainViewModel().angleLeft = angle;
        HhLog.e("左侧摇杆角度 Angle: " + angle + "°");
        obtainViewModel().controlParse();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initRightControl() {
        // 在 View 渲染完毕后获取中心点坐标
        binding.controlRight.post(() -> {
            centerXRight = binding.controlRight.getX() + binding.controlRight.getWidth() / 2f;
            centerYRight = binding.controlRight.getY() + binding.controlRight.getHeight() / 2f;
            baseXRight = binding.controlRight.getX();
            baseYRight = binding.controlRight.getY();
            maxRadiusRight = binding.controlRight.getWidth() * 0.2f; // 限制最大移动范围（摇杆半径 * 1.2）
        });

        binding.controlRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float touchX = event.getRawX();
                float touchY = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDraggingRight = true;
                        startControlRunner();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDraggingRight) {
                            updateJoystickPositionRight(touchX, touchY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDraggingRight = false;
                        obtainViewModel().vyawPost = 0;
                        resetJoystickPositionRight();
                        break;
                }
                return true;
            }
        });
    }

    private float centerXRight, centerYRight, baseXRight, baseYRight;
    private boolean isDraggingRight = false;
    private float maxRadiusRight; // 限制滑动范围

    // 更新摇杆位置，并计算角度
    private void updateJoystickPositionRight(float touchX, float touchY) {
        float deltaX = touchX - centerXRight;
        float deltaY = touchY - centerYRight;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX)); // 计算角度

        // 限制摇杆滑动范围
        if (distance > maxRadiusRight) {
            float scale = (float) (maxRadiusRight / distance);
            deltaX *= scale;
            deltaY *= scale;
        }

        binding.controlRight.setX(baseXRight + deltaX);
        binding.controlRight.setY(baseYRight + deltaY);

        // 角度转换到 0°~360°（右侧 0°，顺时针）
        if (angle < 0) {
            angle += 360;
        }

        onJoystickMoveRight(angle); // 监听角度
    }

    // 摇杆回到初始位置（带动画）
    private void resetJoystickPositionRight() {
        ObjectAnimator animX = ObjectAnimator.ofFloat(binding.controlRight, "x", baseXRight);
        ObjectAnimator animY = ObjectAnimator.ofFloat(binding.controlRight, "y", baseYRight);
        animX.setDuration(300);
        animY.setDuration(300);
        animX.start();
        animY.start();
        obtainViewModel().angleRight = 999;
    }

    // 监听摇杆的角度
    private void onJoystickMoveRight(double angle) {
        obtainViewModel().angleRight = angle;
        HhLog.e("右侧摇杆角度 Angle: " + angle + "°");
        obtainViewModel().controlParse();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initCloudControl() {
        // 在 View 渲染完毕后获取中心点坐标
        binding.controlCloud.post(() -> {
            centerXCloud = binding.controlCloud.getX() + binding.controlCloud.getWidth() / 2f;
            centerYCloud = binding.controlCloud.getY() + binding.controlCloud.getHeight() / 2f;
            baseXCloud = binding.controlCloud.getX();
            baseYCloud = binding.controlCloud.getY();
            maxRadiusCloud = binding.controlCloud.getWidth() * 0.2f; // 限制最大移动范围（摇杆半径 * 1.2）
        });

        binding.controlCloud.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float touchX = event.getRawX();
                float touchY = event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDraggingCloud = true;
                        startControlRunnerCloud();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDraggingCloud) {
                            updateJoystickPositionCloud(touchX, touchY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDraggingCloud = false;
                        obtainViewModel().speed1 = 0;
                        obtainViewModel().speed2 = 0;
                        resetJoystickPositionCloud();
                        break;
                }
                return true;
            }
        });
    }

    private float centerXCloud, centerYCloud, baseXCloud, baseYCloud;
    private boolean isDraggingCloud = false;
    private float maxRadiusCloud; // 限制滑动范围

    // 更新摇杆位置，并计算角度
    private void updateJoystickPositionCloud(float touchX, float touchY) {
        float deltaX = touchX - centerXCloud;
        float deltaY = touchY - centerYCloud;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX)); // 计算角度

        // 限制摇杆滑动范围
        if (distance > maxRadiusCloud) {
            float scale = (float) (maxRadiusCloud / distance);
            deltaX *= scale;
            deltaY *= scale;
        }

        binding.controlCloud.setX(baseXCloud + deltaX);
        binding.controlCloud.setY(baseYCloud + deltaY);

        // 角度转换到 0°~360°（右侧 0°，顺时针）
        if (angle < 0) {
            angle += 360;
        }

        onJoystickMoveCloud(angle); // 监听角度
    }

    // 摇杆回到初始位置（带动画）
    private void resetJoystickPositionCloud() {
        ObjectAnimator animX = ObjectAnimator.ofFloat(binding.controlCloud, "x", baseXCloud);
        ObjectAnimator animY = ObjectAnimator.ofFloat(binding.controlCloud, "y", baseYCloud);
        animX.setDuration(300);
        animY.setDuration(300);
        animX.start();
        animY.start();
        obtainViewModel().angleCloud = 999;
    }

    // 监听摇杆的角度
    private void onJoystickMoveCloud(double angle) {
        obtainViewModel().angleCloud = angle;
        HhLog.e("云台摇杆角度 Angle: " + angle + "°");
        obtainViewModel().controlCloudParse();
    }

    private boolean isRecording = false;
    private Thread recordThread;
    private AudioRecord audioRecord;

    private void startRecordVoice() {
        File dir = new File(getCacheDir() + "/device/" + CommonData.sn, "speaking");
        if (!dir.exists()) dir.mkdirs();

        obtainViewModel().fileName = "speak_" + System.currentTimeMillis() + ".wav";
        obtainViewModel().outputFilePath = new File(dir, obtainViewModel().fileName).getPath();

        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, bufferSize);

        isRecording = true;
        audioRecord.startRecording();

        recordThread = new Thread(() -> {
            FileOutputStream wavOut = null;
            try {
                wavOut = new FileOutputStream(obtainViewModel().outputFilePath);
                writeWavHeader(wavOut, sampleRate, 1, 16); // 预写WAV头

                byte[] buffer = new byte[bufferSize];
                int totalAudioLen = 0;

                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        wavOut.write(buffer, 0, read);
                        totalAudioLen += read;
                    }
                }

                updateWavHeader(obtainViewModel().outputFilePath, totalAudioLen, sampleRate, 1, 16);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (wavOut != null) {
                    try {
                        wavOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        recordThread.start();
    }


    private void writeWavHeader(FileOutputStream out, int sampleRate, int channels, int bitsPerSample) throws IOException {
        byte[] header = new byte[44];

        long byteRate = sampleRate * channels * bitsPerSample / 8;

        // RIFF/WAVE header
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        // 4-7: file size (will update later)
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; // Subchunk1Size for PCM
        header[17] = 0; header[18] = 0; header[19] = 0;
        header[20] = 1; header[21] = 0; // AudioFormat (1 = PCM)
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * bitsPerSample / 8);
        header[33] = 0;
        header[34] = (byte) bitsPerSample;
        header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        // 40-43: data chunk size (will update later)

        out.write(header, 0, 44);
    }

    private void updateWavHeader(String wavPath, int audioLen, int sampleRate, int channels, int bitsPerSample) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(wavPath, "rw");
        long byteRate = sampleRate * channels * bitsPerSample / 8;
        long totalDataLen = audioLen + 36;

        raf.seek(4);
        raf.write((byte) (totalDataLen & 0xff));
        raf.write((byte) ((totalDataLen >> 8) & 0xff));
        raf.write((byte) ((totalDataLen >> 16) & 0xff));
        raf.write((byte) ((totalDataLen >> 24) & 0xff));

        raf.seek(40);
        raf.write((byte) (audioLen & 0xff));
        raf.write((byte) ((audioLen >> 8) & 0xff));
        raf.write((byte) ((audioLen >> 16) & 0xff));
        raf.write((byte) ((audioLen >> 24) & 0xff));

        raf.close();
    }

    private void stopRecordVoice() {
        isRecording = false;

        if (recordThread != null) {
            try {
                recordThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordThread = null;
        }

        if (audioRecord != null) {
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.stop();
            }
            audioRecord.release();
            audioRecord = null;
        }

        obtainViewModel().uploadAudio();
    }



    private void startRecordVoice2() {
        File dir = new File(getCacheDir()+"/device"+"/"+ CommonData.sn, "speaking");
        if (!dir.exists()) dir.mkdirs();
        obtainViewModel().fileName = "speak_"+System.currentTimeMillis() + ".wav";
        obtainViewModel().outputFilePath = new File(dir, obtainViewModel().fileName).getPath();

        obtainViewModel().mediaRecorder = new MediaRecorder();
        obtainViewModel().mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        obtainViewModel().mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        obtainViewModel().mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        obtainViewModel().mediaRecorder.setOutputFile(obtainViewModel().outputFilePath);

        try {
            obtainViewModel().mediaRecorder.prepare();
            obtainViewModel().mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
            HhLog.e("Failed to start recording " + e);
        }
    }

    private void stopRecordVoice2() {
        if (obtainViewModel().mediaRecorder != null) {
            try{
                obtainViewModel().mediaRecorder.stop();
                obtainViewModel().mediaRecorder.release();
                //playMp3();
                obtainViewModel().uploadAudio();
            }catch (Exception e){
                //
            }
        }
    }

    private void playMp3() {
        MediaPlayer player;
        try {
            LibVLC libVLC = new LibVLC(this);
            player = new MediaPlayer(libVLC);
            player.play(obtainViewModel().outputFilePath);

            player.setEventListener(new MediaPlayer.EventListener() {
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    if(event.type == MediaPlayer.Event.EndReached){
                        player.release();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void releasePlayerDog() {
        if (libVLCDog == null || mediaPlayerDog == null ||
                ivlcVoutDog == null || mediaDog == null) {
            return;
        }
        mediaPlayerDog.stop();
        ivlcVoutDog = mediaPlayerDog.getVLCVout();
        ivlcVoutDog.detachViews();
        mediaPlayerDog.release();
        libVLCDog.release();

        libVLCDog = null;
        mediaPlayerDog = null;
        ivlcVoutDog = null;
        mediaDog = null;
    }
    void startPlayerDog() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = binding.dogLive.getWidth();
        int height = binding.dogLive.getHeight();
        releasePlayerDog();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLCDog = new LibVLC(this, options);
        mediaPlayerDog = new MediaPlayer(libVLCDog);
        //设置vlc视频铺满布局
        mediaPlayerDog.setScale(0f);

        mediaPlayerDog.getVLCVout().setWindowSize(width, height);//宽，高  播放窗口的大小
        mediaPlayerDog.setAspectRatio(width+":"+height);//-1，表示完全拉伸填充，不考虑原始比例
//        mediaPlayerDog.setAspectRatio("-1");//-1，表示完全拉伸填充，不考虑原始比例
        mediaPlayerDog.setVolume(0);
        ivlcVoutDog = mediaPlayerDog.getVLCVout();
        ivlcVoutDog.setVideoView(binding.dogLive);
        ivlcVoutDog.attachViews();

        mediaDog = new Media(libVLCDog, Uri.parse(CommonData.dogUrl()));
//        mediaDog.addOption(":rtsp-tcp");//RTSP采用TCP传输方式
        mediaDog.setHWDecoderEnabled(true, true);
        int cache = 150;
        mediaDog.addOption(":network-caching=" + cache);
        mediaDog.addOption(":file-caching=" + cache);
        mediaDog.addOption(":live-cacheing=" + cache);
        mediaDog.addOption(":sout-mux-caching=" + cache);
        mediaDog.addOption(":codec=mediacodec,iomx,all");
        mediaPlayerDog.setMedia(mediaDog);
        mediaPlayerDog.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {

                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        // 处理缓冲事件
                        HhLog.e("Buffering");
                        break;
                    case MediaPlayer.Event.EndReached:
                        // 处理播放结束事件
                        HhLog.e("EndReached");
                        startPlayerDog();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerDog();
                            }catch (Exception e){
                                HhLog.e(e.getMessage());
                            }
                        },1000);
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        // 处理播放进度变化事件
//                        HhLog.e("TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        // 处理播放位置变化事件
//                        HhLog.e("PositionChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
                        HhLog.e("Vout");
                        break;
                }
            }
        });
        mediaPlayerDog.play();
    }

    void releasePlayerLight() {
        if (libVLCLight == null || mediaPlayerLight == null ||
                ivlcVoutLight == null || mediaLight == null) {
            return;
        }
        mediaPlayerLight.stop();
        ivlcVoutLight = mediaPlayerLight.getVLCVout();
        ivlcVoutLight.detachViews();
        mediaPlayerLight.release();
        libVLCLight.release();

        libVLCLight = null;
        mediaPlayerLight = null;
        ivlcVoutLight = null;
        mediaLight = null;
    }
    void startPlayerLight() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = binding.cloudLightLive.getWidth();
        int height = binding.cloudLightLive.getHeight();
        releasePlayerLight();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLCLight = new LibVLC(this, options);
        mediaPlayerLight = new MediaPlayer(libVLCLight);
        //设置vlc视频铺满布局
        mediaPlayerLight.setScale(0f);

        mediaPlayerLight.getVLCVout().setWindowSize(width, height);//宽，高  播放窗口的大小
        mediaPlayerLight.setAspectRatio(width+":"+height);//-1，表示完全拉伸填充，不考虑原始比例
//        mediaPlayerLight.setAspectRatio("-1");//-1，表示完全拉伸填充，不考虑原始比例
        mediaPlayerLight.setVolume(0);
        ivlcVoutLight = mediaPlayerLight.getVLCVout();
        ivlcVoutLight.setVideoView(binding.cloudLightLive);
        ivlcVoutLight.attachViews();

        mediaLight = new Media(libVLCLight, Uri.parse(CommonData.lightUrl()));
//        mediaLight.addOption(":rtsp-tcp");//RTSP采用TCP传输方式
        mediaLight.setHWDecoderEnabled(true, true);
        int cache = 150;
        mediaLight.addOption(":network-caching=" + cache);
        mediaLight.addOption(":file-caching=" + cache);
        mediaLight.addOption(":live-cacheing=" + cache);
        mediaLight.addOption(":sout-mux-caching=" + cache);
        mediaLight.addOption(":codec=mediacodec,iomx,all");
        mediaPlayerLight.setMedia(mediaLight);
        mediaPlayerLight.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {

                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        // 处理缓冲事件
                        HhLog.e("Buffering");
                        break;
                    case MediaPlayer.Event.EndReached:
                        // 处理播放结束事件
                        HhLog.e("EndReached");
                        startPlayerLight();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerLight();
                            }catch (Exception e){
                                HhLog.e(e.getMessage());
                            }
                        },1000);
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        // 处理播放进度变化事件
//                        HhLog.e("TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        // 处理播放位置变化事件
//                        HhLog.e("PositionChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
                        HhLog.e("Vout");
                        break;
                }
            }
        });
        mediaPlayerLight.play();
    }

    void releasePlayerHot() {
        if (libVLCHot == null || mediaPlayerHot == null ||
                ivlcVoutHot == null || mediaHot == null) {
            return;
        }
        mediaPlayerHot.stop();
        ivlcVoutHot = mediaPlayerHot.getVLCVout();
        ivlcVoutHot.detachViews();
        mediaPlayerHot.release();
        libVLCHot.release();

        libVLCHot = null;
        mediaPlayerHot = null;
        ivlcVoutHot = null;
        mediaHot = null;
    }
    void startPlayerHot() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = binding.cloudHotLive.getWidth();
        int height = binding.cloudHotLive.getHeight();
        releasePlayerHot();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLCHot = new LibVLC(this, options);
        mediaPlayerHot = new MediaPlayer(libVLCHot);
        //设置vlc视频铺满布局
        mediaPlayerHot.setScale(0f);

        mediaPlayerHot.getVLCVout().setWindowSize(width, height);//宽，高  播放窗口的大小
        mediaPlayerHot.setAspectRatio(width+":"+height);//-1，表示完全拉伸填充，不考虑原始比例
//        mediaPlayerHot.setAspectRatio("-1");//-1，表示完全拉伸填充，不考虑原始比例
        mediaPlayerHot.setVolume(0);
        ivlcVoutHot = mediaPlayerHot.getVLCVout();
        ivlcVoutHot.setVideoView(binding.cloudHotLive);
        ivlcVoutHot.attachViews();

        mediaHot = new Media(libVLCHot, Uri.parse(CommonData.hotUrl()));
//        mediaHot.addOption(":rtsp-tcp");//RTSP采用TCP传输方式
        mediaHot.setHWDecoderEnabled(true, true);
        int cache = 150;
        mediaHot.addOption(":network-caching=" + cache);
        mediaHot.addOption(":file-caching=" + cache);
        mediaHot.addOption(":live-cacheing=" + cache);
        mediaHot.addOption(":sout-mux-caching=" + cache);
        mediaHot.addOption(":codec=mediacodec,iomx,all");
        mediaPlayerHot.setMedia(mediaHot);
        mediaPlayerHot.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {

                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        // 处理缓冲事件
                        HhLog.e("Buffering");
                        break;
                    case MediaPlayer.Event.EndReached:
                        // 处理播放结束事件
                        HhLog.e("EndReached");
                        startPlayerHot();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerHot();
                            }catch (Exception e){
                                HhLog.e(e.getMessage());
                            }
                        },1000);
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        // 处理播放进度变化事件
//                        HhLog.e("TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        // 处理播放位置变化事件
//                        HhLog.e("PositionChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
                        HhLog.e("Vout");
                        break;
                }
            }
        });
        mediaPlayerHot.play();
    }
}
