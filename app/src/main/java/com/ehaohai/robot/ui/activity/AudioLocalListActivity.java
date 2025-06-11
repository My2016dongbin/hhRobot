package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityAudioLocalListBinding;
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.ui.multitype.AudioLocal;
import com.ehaohai.robot.ui.multitype.AudioLocalViewBinder;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.viewmodel.AudioLocalListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.io.File;
import java.io.IOException;

import me.drakeet.multitype.MultiTypeAdapter;

public class AudioLocalListActivity extends BaseLiveActivity<ActivityAudioLocalListBinding, AudioLocalListViewModel> implements AudioLocalViewBinder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.refresh.setRefreshHeader(new ClassicsHeader(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().getAudioList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        AudioLocalViewBinder audioListViewBinder = new AudioLocalViewBinder(this);
        audioListViewBinder.setListener(this);
        obtainViewModel().adapter.register(AudioLocal.class, audioListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        obtainViewModel().getAudioList();
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.upload, () -> {
//            Toast.makeText(this, "上传", Toast.LENGTH_SHORT).show();
        });
        CommonUtil.click(binding.record, () -> {
            obtainViewModel().recording = !obtainViewModel().recording;
            if(obtainViewModel().recording){
                startRecordVoice();
                obtainViewModel().startRecordTimes();
            }else{
                stopRecordVoice();
                obtainViewModel().stopRecordTimes();
            }
        });
    }

    @Override
    protected ActivityAudioLocalListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_audio_local_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AudioLocalListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AudioLocalListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
        obtainViewModel().recordTimes.observe(this, this::recordTimesChanged);
    }

    private void nameChanged(String name) {

    }

    private void recordTimesChanged(String recordTimes) {
        binding.recordText.setText(recordTimes);
    }

    @Override
    public void onItemClick(AudioLocal audio) {

    }

    @Override
    public void notifyClick(AudioLocal audio) {
        obtainViewModel().getAudioList();
    }

    @Override
    public void onItemDeleteClick(AudioLocal audio) {
        CommonUtil.showConfirm(this,"确认删除该音频吗？", "删除", "取消", new Action() {
            @Override
            public void click() {
                boolean delete = new File(audio.getPath()).delete();
                if(delete){
                    obtainViewModel().getAudioList();
                    Toast.makeText(AudioLocalListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Action() {
            @Override
            public void click() {

            }
        },true);
    }


    private void startRecordVoice() {
        File dir = new File(getCacheDir()+"/device"+"/"+ CommonData.sn, "recordings");
        if (!dir.exists()) dir.mkdirs();
        String fileName = CommonUtil.parseLongTime(System.currentTimeMillis()) + ".mp3";
        obtainViewModel().outputFilePath = new File(dir, fileName).getPath();

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
            obtainViewModel().getAudioList();
        }
    }
}