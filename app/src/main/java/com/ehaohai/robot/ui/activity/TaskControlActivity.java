package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityTaskControlBinding;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.viewmodel.TaskControlViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Objects;

public class TaskControlActivity extends BaseLiveActivity<ActivityTaskControlBinding, TaskControlViewModel> {
    private LibVLC libVLCDog;
    private MediaPlayer mediaPlayerDog;
    private Media mediaDog;
    private IVLCVout ivlcVoutDog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        String stringExtra = getIntent().getStringExtra("task");
        obtainViewModel().task = new Gson().fromJson(stringExtra, new TypeToken<Task>(){}.getType());
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        startPlayerDog();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.start, new Action() {
            @Override
            public void click() {
                if(!Objects.equals(CommonData.taskId, obtainViewModel().task.getTask_id())){
                    ///不是当前机器狗下发任务
                    CommonUtil.showConfirm(TaskControlActivity.this, "确定要下发并立即开始该任务吗？", "确定", "取消", new Action() {
                        @Override
                        public void click() {
                            obtainViewModel().postTaskToDog();
                        }
                    }, new Action() {
                        @Override
                        public void click() {

                        }
                    },false);
                }else{
                    ///正是当前机器狗下发任务
                    if(Objects.equals(CommonData.taskStatus, "1")){
                        //任务执行中
                        Toast.makeText(TaskControlActivity.this, "当前任务正在进行中", Toast.LENGTH_SHORT).show();
                    }else{
                        //任务未执行
                        obtainViewModel().startTask();
                    }
                }
            }
        });
        CommonUtil.click(binding.pause, new Action() {
            @Override
            public void click() {
                ///不是当前机器狗下发任务
                if(!Objects.equals(CommonData.taskId, obtainViewModel().task.getTask_id())){
                    Toast.makeText(TaskControlActivity.this, "您还没有下发该任务", Toast.LENGTH_SHORT).show();
                }else{
                    ///正是当前机器狗下发任务
                    if(Objects.equals(CommonData.taskStatus, "1")){
                        //任务执行中
                        obtainViewModel().pauseTask();
                    }else{
                        //任务未执行
                        Toast.makeText(TaskControlActivity.this, "当前任务还未开始", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        CommonUtil.click(binding.stop, new Action() {
            @Override
            public void click() {
                ///不是当前机器狗下发任务
                if(!Objects.equals(CommonData.taskId, obtainViewModel().task.getTask_id())){
                    Toast.makeText(TaskControlActivity.this, "您还没有下发该任务", Toast.LENGTH_SHORT).show();
                }else{
                    ///正是当前机器狗下发任务
                    if(Objects.equals(CommonData.taskStatus, "1")){
                        //任务执行中
                        obtainViewModel().stopTask();
                    }else{
                        //任务未执行
                        Toast.makeText(TaskControlActivity.this, "当前任务还未开始", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
        int width = binding.live.getWidth();
        int height = binding.live.getHeight();
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
        ivlcVoutDog.setVideoView(binding.live);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayerDog();
    }

    @Override
    protected ActivityTaskControlBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_task_control);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TaskControlViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TaskControlViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}