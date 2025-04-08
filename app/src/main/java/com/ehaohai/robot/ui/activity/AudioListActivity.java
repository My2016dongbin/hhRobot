package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityAudioListBinding;
import com.ehaohai.robot.databinding.ActivityForgetBinding;
import com.ehaohai.robot.ui.viewmodel.AudioListViewModel;
import com.ehaohai.robot.ui.viewmodel.ForgetViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

public class AudioListActivity extends BaseLiveActivity<ActivityAudioListBinding, AudioListViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.upload, () -> {
            Toast.makeText(this, "上传", Toast.LENGTH_SHORT).show();
        });
        CommonUtil.click(binding.record, () -> {
            Toast.makeText(AudioListActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected ActivityAudioListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_audio_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AudioListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AudioListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}