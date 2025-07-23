package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivitySingleWarnListBinding;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.SingleWarnBugViewBinder;
import com.ehaohai.robot.ui.multitype.SingleWarnViewBinder;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.viewmodel.SingleWarnListViewModel;
import com.ehaohai.robot.utils.CommonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class SingleWarnListActivity extends BaseLiveActivity<ActivitySingleWarnListBinding, SingleWarnListViewModel> implements SingleWarnViewBinder.OnItemClickListener, SingleWarnBugViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {
        ///AI报警列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.aiRecycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().aiAdapter = new MultiTypeAdapter(obtainViewModel().aiItems);
        binding.aiRecycle.setHasFixedSize(true);
        binding.aiRecycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.aiRefresh.setRefreshHeader(new ClassicsHeader(this));
        binding.aiRefresh.setRefreshFooter(new ClassicsFooter(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.aiRefresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNumAi = 1;
                obtainViewModel().postWarnList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNumAi++;
                obtainViewModel().postWarnList();
                refreshLayout.finishLoadMore(1000);
            }
        });
        SingleWarnViewBinder warnListViewBinder = new SingleWarnViewBinder(this);
        warnListViewBinder.setListener(this);
        obtainViewModel().aiAdapter.register(Warn.class, warnListViewBinder);
        obtainViewModel().aiAdapter.register(Empty.class, new EmptyViewBinder(this));
        binding.aiRecycle.setAdapter(obtainViewModel().aiAdapter);
        assertHasTheSameAdapter(binding.aiRecycle, obtainViewModel().aiAdapter);
        ///故障报警列表
        LinearLayoutManager linearLayoutManagerBug = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.bugRecycle.setLayoutManager(linearLayoutManagerBug);
        obtainViewModel().bugAdapter = new MultiTypeAdapter(obtainViewModel().bugItems);
        binding.bugRecycle.setHasFixedSize(true);
        binding.bugRecycle.setNestedScrollingEnabled(false);
        binding.bugRefresh.setRefreshHeader(new ClassicsHeader(this));
        binding.bugRefresh.setRefreshFooter(new ClassicsFooter(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.bugRefresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNumBug = 1;
                obtainViewModel().postBugList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNumBug++;
                obtainViewModel().postBugList();
                refreshLayout.finishLoadMore(1000);
            }
        });
        SingleWarnBugViewBinder warnListViewBinderBug = new SingleWarnBugViewBinder(this);
        warnListViewBinderBug.setListener(this);
        obtainViewModel().bugAdapter.register(Warn.class, warnListViewBinderBug);
        obtainViewModel().bugAdapter.register(Empty.class, new EmptyViewBinder(this));
        binding.bugRecycle.setAdapter(obtainViewModel().bugAdapter);
        assertHasTheSameAdapter(binding.bugRecycle, obtainViewModel().bugAdapter);

        obtainViewModel().postWarnList();
        obtainViewModel().postBugList();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        binding.filter.setOnClickListener(view -> {
            binding.warnLayout.openDrawer(GravityCompat.END);
        });
        ///AI报警
        binding.ai.setOnClickListener(view -> {
            if(!obtainViewModel().isAi){
                obtainViewModel().isAi = true;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.bug);

                binding.aiToday.setVisibility(View.VISIBLE);
                binding.aiAll.setVisibility(View.VISIBLE);
                binding.aiList.setVisibility(View.VISIBLE);
                binding.bugList.setVisibility(View.GONE);
            }
        });
        ///故障报警
        binding.bug.setOnClickListener(view -> {
            if(obtainViewModel().isAi){
                obtainViewModel().isAi = false;
                CommonUtil.applyFancyAnimation(view);
                CommonUtil.applyFancyBackAnimation(binding.ai);

                binding.bugList.setVisibility(View.VISIBLE);
                binding.aiToday.setVisibility(View.GONE);
                binding.aiAll.setVisibility(View.GONE);
                binding.aiList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected ActivitySingleWarnListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_single_warn_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SingleWarnListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SingleWarnListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().aiNum.observe(this, this::aiNumChanged);
        obtainViewModel().bugNum.observe(this, this::bugNumChanged);
        obtainViewModel().todayCount.observe(this, this::todayChanged);
        obtainViewModel().allCount.observe(this, this::allChanged);
    }

    private void aiNumChanged(int changed) {
        if(changed > 0){
            binding.unreadAi.setVisibility(View.VISIBLE);
        }else{
            binding.unreadAi.setVisibility(View.GONE);
        }
    }

    private void bugNumChanged(int changed) {
        if(changed > 0){
            binding.unreadBug.setVisibility(View.VISIBLE);
        }else{
            binding.unreadBug.setVisibility(View.GONE);
        }
    }

    private void todayChanged(String changed) {
        binding.todayNumber.setText(changed);
    }

    private void allChanged(String changed) {
        binding.allNumber.setText(changed);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(Warn warn) {
        obtainViewModel().getAlarmRead(warn);
    }

    @Override
    public void onBugItemClick(Warn warn) {
        obtainViewModel().getBugRead(warn);
    }
}