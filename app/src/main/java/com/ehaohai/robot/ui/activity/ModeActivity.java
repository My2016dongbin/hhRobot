package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityModeBinding;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.multitype.WarnBugViewBinder;
import com.ehaohai.robot.ui.multitype.WarnViewBinder;
import com.ehaohai.robot.ui.viewmodel.ModeViewModel;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class ModeActivity extends BaseLiveActivity<ActivityModeBinding, ModeViewModel> implements WarnViewBinder.OnItemClickListener, WarnBugViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {
        obtainViewModel().mode.postValue(CommonData.mode);
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        binding.filter.setOnClickListener(view -> {
            binding.warnLayout.openDrawer(GravityCompat.END);
        });
        binding.modeDefault.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("常规模式");
        });
        binding.modeForefront.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("前沿模式");
        });
        binding.modeAi.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("AI模式");
        });
        binding.statusDefault.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("常规模式");
        });
        binding.statusForefront.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("前沿模式");
        });
        binding.statusAi.setOnClickListener(view -> {
            obtainViewModel().mode.postValue("AI模式");
        });
    }

    @Override
    protected ActivityModeBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_mode);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ModeViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ModeViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().mode.observe(this, this::modeChanged);
    }

    private void modeChanged(String mode) {
        if(Objects.equals(mode, "常规模式")){
            binding.modeDefault.setBackgroundResource(R.drawable.mode_circle);
            binding.modeForefront.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.modeAi.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.statusDefault.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_yes));
            binding.statusForefront.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            binding.statusAi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            CommonData.mode = "常规模式";
        }
        if(Objects.equals(mode, "前沿模式")){
            binding.modeForefront.setBackgroundResource(R.drawable.mode_circle);
            binding.modeDefault.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.modeAi.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.statusForefront.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_yes));
            binding.statusDefault.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            binding.statusAi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            CommonData.mode = "前沿模式";
        }
        if(Objects.equals(mode, "AI模式")){
            binding.modeAi.setBackgroundResource(R.drawable.mode_circle);
            binding.modeDefault.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.modeForefront.setBackgroundResource(R.drawable.mode_circle_trans);
            binding.statusAi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_yes));
            binding.statusDefault.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            binding.statusForefront.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_un));
            CommonData.mode = "AI模式";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(Warn warn) {

    }

    @Override
    public void onBugItemClick(Warn warn) {

    }
}