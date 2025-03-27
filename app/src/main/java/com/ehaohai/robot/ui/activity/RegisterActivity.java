package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityRegisterBinding;
import com.ehaohai.robot.ui.viewmodel.RegisterViewModel;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.ehaohai.robot.utils.StringData;

public class RegisterActivity extends BaseLiveActivity<ActivityRegisterBinding, RegisterViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        binding.usernameEdit.setText((String) SPUtils.get(this, SPValue.userName,""));
        binding.passwordEdit.setText((String)SPUtils.get(this, SPValue.password,""));
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
    }

    @Override
    protected ActivityRegisterBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_register);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public RegisterViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(RegisterViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}