package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityAccountSafeBinding;
import com.ehaohai.robot.databinding.ActivityMineBinding;
import com.ehaohai.robot.ui.viewmodel.AccountSafeViewModel;
import com.ehaohai.robot.ui.viewmodel.MineViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

public class AccountSafeActivity extends BaseLiveActivity<ActivityAccountSafeBinding, AccountSafeViewModel> {

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

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.exit, new Action() {
            @Override
            public void click() {
                CommonUtil.showConfirm(AccountSafeActivity.this, "确认退出当前账户吗？", "退出", "取消", new Action() {
                    @Override
                    public void click() {
                        obtainViewModel().loginOut();
                        ///清空账号数据
                        CommonUtil.accountClear();
                        finish();
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },true);
            }
        });
    }

    @Override
    protected ActivityAccountSafeBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_account_safe);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AccountSafeViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AccountSafeViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }
}