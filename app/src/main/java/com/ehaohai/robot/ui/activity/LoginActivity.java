package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityLoginBinding;
import com.ehaohai.robot.ui.viewmodel.LoginViewModel;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;
import com.ehaohai.robot.utils.StringData;

public class LoginActivity extends BaseLiveActivity<ActivityLoginBinding, LoginViewModel> {

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


        /*///TODO 默认自动登出
        obtainViewModel().loginOut();
        CommonData.offlineModeSN = "";
        CommonData.offlineModeIP = "";*/
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void bind_() {
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
        CommonUtil.click(binding.register, () -> {
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        });
        CommonUtil.click(binding.forget, () -> {
            startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
        });
        CommonUtil.click(binding.loginButton, () -> {
            ///在线模式
            if(binding.usernameEdit.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this, "请输入手机号或邮箱地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.passwordEdit.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!obtainViewModel().confirm){
                Toast.makeText(LoginActivity.this, "请先阅读并同意协议声明", Toast.LENGTH_SHORT).show();
                return;
            }
            ///TODO 测试
            SPUtils.put(HhApplication.getInstance(), SPValue.userName, binding.usernameEdit.getText().toString());
            SPUtils.put(HhApplication.getInstance(), SPValue.password, binding.passwordEdit.getText().toString());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
        binding.offline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CommonData.networkMode = !b;
                if(b){
                    startActivity(new Intent(LoginActivity.this, DeviceSearchActivity.class));
                }else{
                    /*TODO 登出
                    obtainViewModel().loginOut();
                    CommonData.offlineModeSN = "";
                    CommonData.offlineModeIP = "";*/
                }
            }
        });
        CommonUtil.click(binding.confirm, () -> {
            obtainViewModel().confirm = !obtainViewModel().confirm;
            if(obtainViewModel().confirm){
                binding.confirm.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));
            }else{
                binding.confirm.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.offline.setChecked(false);
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

    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private boolean isExit = false;
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次回到主页", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        } else {
            finishAffinity();
        }
    }
}