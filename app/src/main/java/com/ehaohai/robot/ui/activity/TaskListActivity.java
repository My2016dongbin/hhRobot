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
import com.ehaohai.robot.databinding.ActivityTaskListBinding;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.multitype.WarnBugViewBinder;
import com.ehaohai.robot.ui.multitype.TaskViewBinder;
import com.ehaohai.robot.ui.viewmodel.TaskListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

public class TaskListActivity extends BaseLiveActivity<ActivityTaskListBinding, TaskListViewModel> implements TaskViewBinder.OnItemClickListener, WarnBugViewBinder.OnItemClickListener {
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
                obtainViewModel().pageNum = 1;
                obtainViewModel().postTaskList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNum++;
                obtainViewModel().postTaskList();
                refreshLayout.finishLoadMore(1000);
            }
        });
        TaskViewBinder taskListViewBinder = new TaskViewBinder(this);
        taskListViewBinder.setListener(this);
        obtainViewModel().aiAdapter.register(Task.class, taskListViewBinder);
        obtainViewModel().aiAdapter.register(Empty.class, new EmptyViewBinder(this));
        binding.aiRecycle.setAdapter(obtainViewModel().aiAdapter);
        assertHasTheSameAdapter(binding.aiRecycle, obtainViewModel().aiAdapter);

        obtainViewModel().postTaskList();
    }

    private void bind_() {
        CommonUtil.click(binding.back, new Action() {
            @Override
            public void click() {
                finish();
            }
        });
        CommonUtil.click(binding.confirm, new Action() {
            @Override
            public void click() {
                binding.warnLayout.closeDrawer(GravityCompat.END);
            }
        });
        CommonUtil.click(binding.add, new Action() {
            @Override
            public void click() {
                binding.warnLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    protected ActivityTaskListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_task_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TaskListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TaskListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().warn.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(Task warn) {

    }

    @Override
    public void onStartClick(Task task) {

    }

    @Override
    public void onEditClick(Task task) {

    }

    @Override
    public void onDeleteClick(Task task) {
        CommonUtil.showConfirm(this,"确认删除当前任务吗？", "删除", "取消", new Action() {
            @Override
            public void click() {

            }
        }, new Action() {
            @Override
            public void click() {

            }
        },true);
    }

    @Override
    public void onBugItemClick(Warn warn) {

    }
}