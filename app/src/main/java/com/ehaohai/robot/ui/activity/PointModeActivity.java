package com.ehaohai.robot.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPointModeBinding;
import com.ehaohai.robot.model.Heart;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.viewmodel.PointModeViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class PointModeActivity extends BaseLiveActivity<ActivityPointModeBinding, PointModeViewModel> {
    private LibVLC libVLCDog;
    private MediaPlayer mediaPlayerDog;
    private Media mediaDog;
    private IVLCVout ivlcVoutDog;
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
        binding.temperature.setText(heart.getAmbTemperature()+"°C");
    }

    private void init_() {
        initLeftControl();
        initRightControl();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlayerDog();
            }
        },200);
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.pointStart, new Action() {
            @Override
            public void click() {
                boolean value = Boolean.TRUE.equals(obtainViewModel().start.getValue());
                if(!value){
                    //开始
                    obtainViewModel().start.postValue(true);
                    obtainViewModel().startPoint();
                }else{
                    //停止
                    obtainViewModel().start.postValue(false);
                    obtainViewModel().stopPoint(new Action() {
                        @Override
                        public void click() {
                            finish();
                        }
                    });
                }
            }
        });
        CommonUtil.click(binding.pointAdd, new Action() {
            @Override
            public void click() {
                //打点
                boolean value = Boolean.TRUE.equals(obtainViewModel().start.getValue());
                if(!value){
                    Toast.makeText(PointModeActivity.this, "请先开启打点模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                showNameDialog("", new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("name",text);
                            obtainViewModel().addPoint(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void showNameDialog(String defaultName, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);

        tvTitle.setText("请输入打点名称");
        etInput.setText(defaultName);
        etInput.setSelection(defaultName.length());

        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvConfirm.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                listener.onConfirm(input);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "打点名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            // 设置为屏幕宽度的 85% 可自适应不同机型
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
            window.setAttributes(params);
        }
    }

    @Override
    protected ActivityPointModeBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_point_mode);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PointModeViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PointModeViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
        obtainViewModel().start.observe(this, this::startChanged);
    }

    private void nameChanged(String name) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startChanged(boolean start) {
        binding.pointStart.setImageDrawable(getResources().getDrawable(start?R.drawable.map_stop:R.drawable.point_add));
        binding.tag.setVisibility(start?View.VISIBLE:View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        if(isDragging || isDraggingRight){
            obtainViewModel().controlPost();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startControlRunner();
                }
            },200);
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

}
