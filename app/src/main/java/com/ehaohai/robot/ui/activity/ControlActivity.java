package com.ehaohai.robot.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityControlBinding;
import com.ehaohai.robot.ui.viewmodel.ControlViewModel;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.NetworkSpeedMonitor;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ControlActivity extends BaseLiveActivity<ActivityControlBinding, ControlViewModel> {
    BatteryReceiver batteryReceiver;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Media media;
    private IVLCVout ivlcVout;
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

        initLeftControl();
        initRightControl();

        startPlayer();
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        //播放器
        binding.llPlayer.setOnClickListener(view -> {
            startActivity(new Intent(this,AudioListActivity.class));
        });
        //设置
        binding.setting.setOnClickListener(view -> {
            binding.settingLayout.openDrawer(GravityCompat.END);
        });
        ///急停
        binding.stop.setOnClickListener(view -> {
            stopAnimation();
            if(obtainViewModel().stop){
                obtainViewModel().sportControl("manual","es","");
                hideOtherButton(view);
                hideOtherButtonStatus(view);
            }else{
                obtainViewModel().sportControl("manual","recover","");
            }
        });
        ///报警
        CommonUtil.click(binding.warn, () -> {
            startActivity(new Intent(ControlActivity.this,WarnListActivity.class));
        });
        ///对讲
        binding.speak.setOnClickListener(view -> {
            obtainViewModel().speak = !obtainViewModel().speak;
            if(obtainViewModel().speak){
                CommonUtil.applyFancyAnimation(view);
                voiceAnimation();
            }else{
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
            if(!obtainViewModel().isDog){
                obtainViewModel().isDog = true;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.cloud);
            }
        });
        ///云台
        binding.cloud.setOnClickListener(view -> {
            if(obtainViewModel().isDog){
                obtainViewModel().isDog = false;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.dog);
            }
        });
        ///避障
        binding.force.setOnClickListener(view -> {
            obtainViewModel().force = !obtainViewModel().force;
            if(obtainViewModel().force){
                CommonUtil.applyFancyAnimation(view);
                obtainViewModel().sportControl("manual","obstacle","ON");
            }else{
                CommonUtil.applyFancyBackAnimation(view);
                obtainViewModel().sportControl("manual","obstacle","OFF");
            }
        });
        ///通知
        CommonUtil.click(binding.notice, () -> {
            Toast.makeText(ControlActivity.this, "通知", Toast.LENGTH_SHORT).show();
        });
        ///截图
        CommonUtil.click(binding.screenshoot, () ->{
            Toast.makeText(ControlActivity.this, "截图已保存", Toast.LENGTH_SHORT).show();
        });
        ///录像
        CommonUtil.click(binding.record, () -> {
            obtainViewModel().record = !obtainViewModel().record;
            if(obtainViewModel().record){
                binding.videoCount.setVisibility(View.VISIBLE);
                binding.record.setBackgroundResource(R.drawable.circle_line_red);
                binding.recordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                //开始计时并录制
                Toast.makeText(ControlActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
                obtainViewModel().startRecordTimes();
            }else{
                binding.videoCount.setVisibility(View.GONE);
                binding.record.setBackgroundResource(R.drawable.circle_line_blue);
                binding.recordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_un));
                //关闭计时并保存录像
                Toast.makeText(ControlActivity.this, "录像已保存", Toast.LENGTH_SHORT).show();
                obtainViewModel().stopRecordTimes();
            }
        });
        ///翻身
        binding.fanShen.setOnClickListener(view -> {
            obtainViewModel().fanShen = true;
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///伸懒腰
        binding.shenLanYao.setOnClickListener(view -> {
            obtainViewModel().shenLanYao = true;
            obtainViewModel().sportControl("manual","sport","Stretch");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///握手
        binding.woShou.setOnClickListener(view -> {
            obtainViewModel().woShou = true;
            obtainViewModel().sportControl("manual","sport","Hello");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///比心
        binding.biXin.setOnClickListener(view -> {
            obtainViewModel().biXin = true;
            obtainViewModel().sportControl("manual","sport","content");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///扑人
        binding.puRen.setOnClickListener(view -> {
            obtainViewModel().puRen = true;
            obtainViewModel().sportControl("manual","sport","FrontPounce");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///跳
        binding.jump.setOnClickListener(view -> {
            obtainViewModel().jump = true;
            obtainViewModel().sportControl("manual","sport","FrontJump");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButton(view);
        });
        ///阻尼
        binding.zuNi.setOnClickListener(view -> {
            obtainViewModel().zuNi = true;
            obtainViewModel().sportControl("manual","sport","Damp");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///站立
        binding.zhanLi.setOnClickListener(view -> {
            obtainViewModel().zhanLi = true;
            obtainViewModel().sportControl("manual","sport","BalanceStand");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///坐下
        binding.zuoXia.setOnClickListener(view -> {
            obtainViewModel().zuoXia = true;
            obtainViewModel().sportControl("manual","sport","Sit");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///卧倒
        binding.woDao.setOnClickListener(view -> {
            obtainViewModel().woDao = true;
            obtainViewModel().sportControl("manual","sport","StandDown");
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///锁定
        binding.lock.setOnClickListener(view -> {
            obtainViewModel().lock = true;
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
        ///摆姿势
        binding.baiZiShi.setOnClickListener(view -> {
            obtainViewModel().baiZiShi = true;
            CommonUtil.applyFancyAnimation(view);
            hideOtherButtonStatus(view);
        });
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
        },200);
    }

    private void hideOtherButton(View view) {
        if(obtainViewModel().fanShen && view.getId()!=binding.fanShen.getId()){
            CommonUtil.applyFancyBackAnimation(binding.fanShen);
            obtainViewModel().fanShen = false;
        }
        if(obtainViewModel().shenLanYao && view.getId()!=binding.shenLanYao.getId()){
            CommonUtil.applyFancyBackAnimation(binding.shenLanYao);
            obtainViewModel().shenLanYao = false;
        }
        if(obtainViewModel().woShou && view.getId()!=binding.woShou.getId()){
            CommonUtil.applyFancyBackAnimation(binding.woShou);
            obtainViewModel().woShou = false;
        }
        if(obtainViewModel().biXin && view.getId()!=binding.biXin.getId()){
            CommonUtil.applyFancyBackAnimation(binding.biXin);
            obtainViewModel().biXin = false;
        }
        if(obtainViewModel().puRen && view.getId()!=binding.puRen.getId()){
            CommonUtil.applyFancyBackAnimation(binding.puRen);
            obtainViewModel().puRen = false;
        }
        if(obtainViewModel().jump && view.getId()!=binding.jump.getId()){
            CommonUtil.applyFancyBackAnimation(binding.jump);
            obtainViewModel().jump = false;
        }
    }

    private void hideOtherButtonStatus(View view) {
        if(obtainViewModel().zuNi && view.getId()!=binding.zuNi.getId()){
            CommonUtil.applyFancyBackAnimation(binding.zuNi);
            obtainViewModel().zuNi = false;
        }
        if(obtainViewModel().zhanLi && view.getId()!=binding.zhanLi.getId()){
            CommonUtil.applyFancyBackAnimation(binding.zhanLi);
            obtainViewModel().zhanLi = false;
        }
        if(obtainViewModel().zuoXia && view.getId()!=binding.zuoXia.getId()){
            CommonUtil.applyFancyBackAnimation(binding.zuoXia);
            obtainViewModel().zuoXia = false;
        }
        if(obtainViewModel().woDao && view.getId()!=binding.woDao.getId()){
            CommonUtil.applyFancyBackAnimation(binding.woDao);
            obtainViewModel().woDao = false;
        }
        if(obtainViewModel().lock && view.getId()!=binding.lock.getId()){
            CommonUtil.applyFancyBackAnimation(binding.lock);
            obtainViewModel().lock = false;
        }
        if(obtainViewModel().baiZiShi && view.getId()!=binding.baiZiShi.getId()){
            CommonUtil.applyFancyBackAnimation(binding.baiZiShi);
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

        obtainViewModel().recordTimes.observe(this, this::recordTimesChanged);
        obtainViewModel().voiceTimes.observe(this, this::voiceTimesChanged);
    }

    private void recordTimesChanged(String recordTimes) {
        binding.videoCount.setText(recordTimes);
    }

    private void voiceTimesChanged(String recordTimes) {
        binding.voiceCount.setText(recordTimes);
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
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            updateJoystickPosition(touchX, touchY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        resetJoystickPosition();
                        break;
                }
                return true;
            }
        });
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
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isDraggingRight) {
                            updateJoystickPositionRight(touchX, touchY);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        isDraggingRight = false;
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



    private void startRecordVoice() {
        obtainViewModel().outputFilePath = getCacheDir() + "/voice_recording"+new Random(10000).nextInt()+".mp3";

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

    private void stopRecordVoice() {
        if (obtainViewModel().mediaRecorder != null) {
            obtainViewModel().mediaRecorder.stop();
            obtainViewModel().mediaRecorder.release();
            Toast.makeText(this, "已发送", Toast.LENGTH_SHORT).show();
            playMp3();
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


    void releasePlayer() {
        if (libVLC == null || mediaPlayer == null ||
                ivlcVout == null || media == null) {
            return;
        }
        mediaPlayer.stop();
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.detachViews();
        mediaPlayer.release();
        libVLC.release();

        libVLC = null;
        mediaPlayer = null;
        ivlcVout = null;
        media = null;
    }

    void startPlayer() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        releasePlayer();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        //设置vlc视频铺满布局
        mediaPlayer.setScale(2f);

        mediaPlayer.getVLCVout().setWindowSize(width, height);//宽，高  播放窗口的大小
        mediaPlayer.setAspectRatio("-1");//-1，表示完全拉伸填充，不考虑原始比例
        mediaPlayer.setVolume(0);
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(binding.dogLive);
        ivlcVout.attachViews();

        media = new Media(libVLC, Uri.parse(obtainViewModel().liveUrl));
        media.addOption(":network-caching=500");//网络缓存
        media.addOption(":rtsp-tcp");//RTSP采用TCP传输方式
        media.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media.addOption(":network-caching=" + cache);
        media.addOption(":file-caching=" + cache);
        media.addOption(":live-cacheing=" + cache);
        media.addOption(":sout-mux-caching=" + cache);
        media.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer.setMedia(media);
        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayer();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayer();
                            }catch (Exception e){
                                HhLog.e(e.getMessage());
                            }
                        },1000);
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        // 处理播放进度变化事件
                        HhLog.e("TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        // 处理播放位置变化事件
                        HhLog.e("PositionChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
                        HhLog.e("Vout");
                        break;
                }
            }
        });
        mediaPlayer.play();
    }
}
