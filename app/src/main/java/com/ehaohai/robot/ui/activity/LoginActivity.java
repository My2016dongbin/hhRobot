package com.ehaohai.robot.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityLoginBinding;
import com.ehaohai.robot.ui.viewmodel.LoginViewModel;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.ehaohai.robot.utils.StringData;

public class LoginActivity extends BaseLiveActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().getName();
    }

    private void init_() {
        binding.usernameEdit.setText((String) SPUtils.get(this, SPValue.userName,""));
        binding.passwordEdit.setText((String)SPUtils.get(this, SPValue.password,""));
    }

    private void bind_() {
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.usernameEdit.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, StringData.login_name_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.passwordEdit.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, StringData.login_password_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                //登录
                obtainViewModel().login(binding.usernameEdit.getText().toString(),binding.passwordEdit.getText().toString());
            }
        });
    }

    @Override
    protected ActivityLoginBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LoginViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LoginViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {
//        binding.name.setText(name);
    }
}