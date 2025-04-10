package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityOfflineLoginBinding;
import com.ehaohai.robot.ui.viewmodel.OfflineLoginViewModel;
import com.ehaohai.robot.utils.CommonUtil;

public class OfflineLoginActivity extends BaseLiveActivity<ActivityOfflineLoginBinding, OfflineLoginViewModel> {

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.eye, () -> {
            obtainViewModel().eye = !obtainViewModel().eye;
            if(obtainViewModel().eye){
                binding.eye.setImageDrawable(getResources().getDrawable(R.drawable.ic_zheng));
                binding.passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else{
                binding.eye.setImageDrawable(getResources().getDrawable(R.drawable.ic_bi));
                binding.passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        CommonUtil.click(binding.loginButton, () -> {
            ///离线模式
            if(binding.usernameEdit.getText().toString().isEmpty()){
                Toast.makeText(OfflineLoginActivity.this, "请输入手机号或邮箱地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.passwordEdit.getText().toString().isEmpty()){
                Toast.makeText(OfflineLoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            obtainViewModel().login(binding.usernameEdit.getText().toString(),binding.passwordEdit.getText().toString());
        });
    }

    @Override
    protected ActivityOfflineLoginBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_offline_login);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public OfflineLoginViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(OfflineLoginViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}