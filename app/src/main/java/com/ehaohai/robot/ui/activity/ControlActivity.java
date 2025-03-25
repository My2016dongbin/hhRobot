package com.ehaohai.robot.ui.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityControlBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.ui.viewmodel.ControlViewModel;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.NetworkSpeedMonitor;

import org.greenrobot.eventbus.EventBus;

public class ControlActivity extends BaseLiveActivity<ActivityControlBinding, ControlViewModel> {
    BatteryReceiver batteryReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
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
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        binding.stop.setOnClickListener(view -> {
            stopAnimation();
            if(obtainViewModel().stop){
                hideOtherButton(view);
                hideOtherButtonStatus(view);
            }
        });
        binding.warn.setOnClickListener(view -> {
            applyClickAnimation(view);
            Toast.makeText(ControlActivity.this, "报警", Toast.LENGTH_SHORT).show();
        });
        binding.speak.setOnClickListener(view -> {
            applyClickAnimation(view);
            Toast.makeText(ControlActivity.this, "对讲", Toast.LENGTH_SHORT).show();
        });
        binding.dog.setOnClickListener(view -> {
            if(!obtainViewModel().isDog){
                obtainViewModel().isDog = true;
                applyFancyAnimation(view);
                applyFancyBackAnimation(binding.cloud);
            }
        });
        binding.cloud.setOnClickListener(view -> {
            if(obtainViewModel().isDog){
                obtainViewModel().isDog = false;
                applyFancyAnimation(view);
                applyFancyBackAnimation(binding.dog);
            }
        });
        binding.force.setOnClickListener(view -> {
            obtainViewModel().force = !obtainViewModel().force;
            if(obtainViewModel().force){
                applyFancyAnimation(view);
            }else{
                applyFancyBackAnimation(view);
            }
        });
        binding.notice.setOnClickListener(view -> {
            applyClickAnimation(view);
            Toast.makeText(ControlActivity.this, "通知", Toast.LENGTH_SHORT).show();
        });
        binding.screenshoot.setOnClickListener(view -> {
            applyClickAnimation(view);
            Toast.makeText(ControlActivity.this, "截图", Toast.LENGTH_SHORT).show();
        });
        binding.record.setOnClickListener(view -> {
            obtainViewModel().record = !obtainViewModel().record;
            applyClickAnimation(view);
            if(obtainViewModel().record){
                binding.videoCount.setVisibility(View.VISIBLE);
                //开始计时并录制
                Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
            }else{
                binding.videoCount.setVisibility(View.GONE);
                //关闭计时并保存录像
                Toast.makeText(this, "录像已保存", Toast.LENGTH_SHORT).show();
            }
        });
        binding.fanShen.setOnClickListener(view -> {
            obtainViewModel().fanShen = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.shenLanYao.setOnClickListener(view -> {
            obtainViewModel().shenLanYao = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.woShou.setOnClickListener(view -> {
            obtainViewModel().woShou = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.biXin.setOnClickListener(view -> {
            obtainViewModel().biXin = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.puRen.setOnClickListener(view -> {
            obtainViewModel().puRen = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.jump.setOnClickListener(view -> {
            obtainViewModel().jump = true;
            applyFancyAnimation(view);
            hideOtherButton(view);
        });
        binding.zuNi.setOnClickListener(view -> {
            obtainViewModel().zuNi = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        binding.zhanLi.setOnClickListener(view -> {
            obtainViewModel().zhanLi = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        binding.zuoXia.setOnClickListener(view -> {
            obtainViewModel().zuoXia = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        binding.woDao.setOnClickListener(view -> {
            obtainViewModel().woDao = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        binding.lock.setOnClickListener(view -> {
            obtainViewModel().lock = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        binding.baiZiShi.setOnClickListener(view -> {
            obtainViewModel().baiZiShi = true;
            applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
    }

    private void hideOtherButton(View view) {
        if(obtainViewModel().fanShen && view.getId()!=binding.fanShen.getId()){
            applyFancyBackAnimation(binding.fanShen);
            obtainViewModel().fanShen = false;
        }
        if(obtainViewModel().shenLanYao && view.getId()!=binding.shenLanYao.getId()){
            applyFancyBackAnimation(binding.shenLanYao);
            obtainViewModel().shenLanYao = false;
        }
        if(obtainViewModel().woShou && view.getId()!=binding.woShou.getId()){
            applyFancyBackAnimation(binding.woShou);
            obtainViewModel().woShou = false;
        }
        if(obtainViewModel().biXin && view.getId()!=binding.biXin.getId()){
            applyFancyBackAnimation(binding.biXin);
            obtainViewModel().biXin = false;
        }
        if(obtainViewModel().puRen && view.getId()!=binding.puRen.getId()){
            applyFancyBackAnimation(binding.puRen);
            obtainViewModel().puRen = false;
        }
        if(obtainViewModel().jump && view.getId()!=binding.jump.getId()){
            applyFancyBackAnimation(binding.jump);
            obtainViewModel().jump = false;
        }
    }

    private void hideOtherButtonStatus(View view) {
        if(obtainViewModel().zuNi && view.getId()!=binding.zuNi.getId()){
            applyFancyBackAnimation(binding.zuNi);
            obtainViewModel().zuNi = false;
        }
        if(obtainViewModel().zhanLi && view.getId()!=binding.zhanLi.getId()){
            applyFancyBackAnimation(binding.zhanLi);
            obtainViewModel().zhanLi = false;
        }
        if(obtainViewModel().zuoXia && view.getId()!=binding.zuoXia.getId()){
            applyFancyBackAnimation(binding.zuoXia);
            obtainViewModel().zuoXia = false;
        }
        if(obtainViewModel().woDao && view.getId()!=binding.woDao.getId()){
            applyFancyBackAnimation(binding.woDao);
            obtainViewModel().woDao = false;
        }
        if(obtainViewModel().lock && view.getId()!=binding.lock.getId()){
            applyFancyBackAnimation(binding.lock);
            obtainViewModel().lock = false;
        }
        if(obtainViewModel().baiZiShi && view.getId()!=binding.baiZiShi.getId()){
            applyFancyBackAnimation(binding.baiZiShi);
            obtainViewModel().baiZiShi = false;
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

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            binding.horizontalBattery.setPower(CommonUtil.parseInt(batteryPercentage+""));
        }
    }



    //缩放渐变动画
    private void applyClickAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f, 1.0f, 0.95f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatMode(ScaleAnimation.REVERSE);
        scaleAnimation.setRepeatCount(1);
        view.startAnimation(scaleAnimation);
    }

    //背景切换动画
    @SuppressLint("UseCompatLoadingForDrawables")
    private void applyFancyAnimation(View view) {
        // **1. 背景颜色渐变**
        Drawable[] layers;
        layers = new Drawable[]{
                ContextCompat.getDrawable(this, R.drawable.circle_gray),
                ContextCompat.getDrawable(this, R.drawable.circle_blue)
        };

        // 创建背景渐变效果的 TransitionDrawable
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(500); // 背景切换渐变 500ms

        // **2. 按钮点击时的缩放动画（轻微缩小再弹回）**
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f, 1f);

        // **3. 旋转动画（轻微旋转增加动感）**
        //PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 2f, -2f, 0f);

        // **4. 透明度动画（微闪效果）**
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.6f, 1f);

        // 组合动画
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, /*rotation,*/ alpha);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // 平滑过渡
        animator.setDuration(600);
        animator.start();
    }

    //背景切换动画
    @SuppressLint("UseCompatLoadingForDrawables")
    private void applyFancyBackAnimation(View view) {
        // **1. 背景颜色渐变**
        Drawable[] layers;
        layers = new Drawable[]{
                ContextCompat.getDrawable(this, R.drawable.circle_blue),
                ContextCompat.getDrawable(this, R.drawable.circle_gray)
        };

        // 创建背景渐变效果的 TransitionDrawable
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(500); // 背景切换渐变 500ms

        // **2. 按钮点击时的缩放动画（轻微缩小再弹回）**
        //PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f, 1f);
        //PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f, 1f);

        // **3. 旋转动画（轻微旋转增加动感）**
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 2f, -2f, 0f);

        // **4. 透明度动画（微闪效果）**
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.6f, 1f);

        // 组合动画
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, /*scaleX, scaleY,*/ rotation, alpha);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // 平滑过渡
        animator.setDuration(600);
        animator.start();
    }

    //急停切换动画（展开/收起）
    private void stopAnimation() {
        float targetTranslationX = obtainViewModel().stop ? obtainViewModel().stopDistance : 0;
        obtainViewModel().stop = !obtainViewModel().stop;

        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.stop, "translationX", targetTranslationX);
        animator.setDuration(500); // 动画时间 500ms
        animator.start();
    }
}
