package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityTaskListBinding;
import com.ehaohai.robot.event.TaskRoute;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.multitype.WarnBugViewBinder;
import com.ehaohai.robot.ui.multitype.TaskViewBinder;
import com.ehaohai.robot.ui.viewmodel.TaskListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class TaskListActivity extends BaseLiveActivity<ActivityTaskListBinding, TaskListViewModel> implements TaskViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///接收任务路径
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(TaskRoute taskRoute) {
        obtainViewModel().routeList = taskRoute.getRoute();
        HhLog.e("CommonData.routeList " + CommonData.routeList.size() + "," + CommonData.routeList);
        //处理任务路径-回显
        parseRouteToShow();
    }
    ///任务刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Task task) {
        obtainViewModel().postTaskListLocal();
    }

    private void parseRouteToShow() {
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < obtainViewModel().routeList.size(); i++) {
            Point point = obtainViewModel().routeList.get(i);
            if(names.length() == 0){
                names.append(point.getName());
            }else{
                names.append(";").append(point.getName());
            }
        }
        obtainViewModel().routeNames = names.toString();
        binding.tapRouteText.setText(obtainViewModel().routeNames);
    }

    private void init_() {
        ///任务列表
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
                obtainViewModel().postTaskListLocal();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //obtainViewModel().pageNum++;
                obtainViewModel().postTaskListLocal();
                refreshLayout.finishLoadMore(1000);
            }
        });
        TaskViewBinder taskListViewBinder = new TaskViewBinder(this);
        taskListViewBinder.setListener(this);
        obtainViewModel().aiAdapter.register(Task.class, taskListViewBinder);
        obtainViewModel().aiAdapter.register(Empty.class, new EmptyViewBinder(this));
        binding.aiRecycle.setAdapter(obtainViewModel().aiAdapter);
        assertHasTheSameAdapter(binding.aiRecycle, obtainViewModel().aiAdapter);

        obtainViewModel().postTaskListLocal();
    }

    private void bind_() {
        binding.warnLayout.setScrimColor(getResources().getColor(R.color.transparent));
        CommonUtil.click(binding.back, new Action() {
            @Override
            public void click() {
                finish();
            }
        });
        /*CommonUtil.click(binding.pointManage, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(TaskListActivity.this,PointManageActivity.class));
            }
        });*/
        CommonUtil.click(binding.add, new Action() {
            @SuppressLint("SetTextI18n")
            @Override
            public void click() {
                binding.drawerNameEdit.setText("");
                obtainViewModel().aroundHour.postValue(2);
                obtainViewModel().hour = 2;
                binding.tapAroundText.setText(obtainViewModel().hour+"小时");
                binding.tapStatusText.setText(CommonUtil.parseTaskStatusShow(""));
                binding.tapRouteText.setText("");
                obtainViewModel().routeList.clear();
                binding.tapStartTimeText.setText("");

                obtainViewModel().drawerState.postValue(1);
                binding.warnLayout.openDrawer(GravityCompat.END);
            }
        });
        CommonUtil.click(binding.addConfirm, new Action() {
            @Override
            public void click() {
                if(binding.drawerNameEdit.getText().toString().isEmpty()){
                    Toast.makeText(TaskListActivity.this, "请输入任务名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.tapRouteText.getText().toString().isEmpty()){
                    Toast.makeText(TaskListActivity.this, "请选择任务路径", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.tapStartTimeText.getText().toString().isEmpty()){
                    Toast.makeText(TaskListActivity.this, "请选择任务开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.warnLayout.closeDrawer(GravityCompat.END);
                int value = obtainViewModel().drawerState.getValue();
                if(value == 1){
                    ///添加
                    obtainViewModel().postAddTaskOnline(binding.drawerNameEdit.getText().toString(),binding.tapStartTimeText.getText().toString());
                }
                if(value == 2){
                    ///修改
                    Task task = new Task();
                    List<Task.Route> routeArrayList = new ArrayList<>();
                    for (int i = 0; i < obtainViewModel().routeList.size(); i++) {
                        Point point = obtainViewModel().routeList.get(i);
                        Task.Route route = new Task.Route();
                        route.setPoint_index(Integer.parseInt(point.getId()+""));
                        route.setPoi_name(point.getName()+"");
                        route.setPoint_type(point.getType()+"");
                        route.setLift_param(point.getTaskFloor());
                        route.setX(point.getX());
                        route.setY(point.getY());
                        route.setZ(point.getZ());
                        routeArrayList.add(route);
                    }
                    task.setTask_id(obtainViewModel().task.getTask_id());
                    task.setTask_name(binding.drawerNameEdit.getText().toString());
                    task.setStart_time(binding.tapStartTimeText.getText().toString());
                    task.setTask_timer(obtainViewModel().hour*60*60);
                    task.setTask_route(routeArrayList);
                    CommonUtil.editRobotTask(CommonData.sn,task);
                    obtainViewModel().delayTaskListLocal();
                }
            }
        });
        CommonUtil.click(binding.tapLines, new Action() {
            @Override
            public void click() {
                ///任务路径选择
                if(obtainViewModel().routeList.isEmpty()){
                    CommonData.routeList = new ArrayList<>();
                }else{
                    CommonData.routeList = new ArrayList<>(obtainViewModel().routeList);
                    HhLog.e("CommonData.routeList " + CommonData.routeList);
                }
                startActivity(new Intent(TaskListActivity.this,TaskRouteActivity.class));
            }
        });
        CommonUtil.click(binding.tapStartTime, new Action() {
            @Override
            public void click() {
                TimePickerView pvTime = new TimePickerBuilder(TaskListActivity.this, (date, v) -> {
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
                    binding.tapStartTimeText.setText(time);
                })
                        .setType(new boolean[]{true, true, true, true, true, true})
                        .setTitleText("请选择任务开始时间")
                        .setCancelText("取消")
                        .setSubmitText("确定")
                        .setContentTextSize(16)
                        .setLineSpacingMultiplier(2.0f)
                        .setItemVisibleCount(5)
                        .isDialog(true)
                        .setOutSideCancelable(true)
                        .setBgColor(Color.TRANSPARENT) // 设置背景透明，由 Dialog 控制圆角背景
                        .setTitleBgColor(Color.TRANSPARENT)
                        .setTitleColor(Color.WHITE)
                        .setCancelColor(Color.LTGRAY)
                        .setSubmitColor(Color.WHITE)
                        .setTextColorCenter(Color.WHITE)
                        .setDecorView(null) // 让它弹在 decorView 中
                        .build();
                Dialog dialog = pvTime.getDialog();
                if (dialog != null) {
                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setGravity(Gravity.CENTER); // 中间弹出
                        window.setWindowAnimations(R.style.picker_view_slide_anim); // 弹出动画，可选

                        // 设置圆角背景
                        window.setBackgroundDrawableResource(R.drawable.bg_picker_dialog_round);

                        // 可选设置宽高
                        WindowManager.LayoutParams params = window.getAttributes();
                        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8); // 屏幕宽度 80%
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        window.setAttributes(params);
                    }

                    dialog.show();
                }
            }
        });
        CommonUtil.click(binding.tapAround, new Action() {
            @Override
            public void click() {
                int value = obtainViewModel().drawerState.getValue();
                if(value==1){
                    obtainViewModel().drawerState.postValue(3);
                }else{
                    obtainViewModel().drawerState.postValue(4);
                }
            }
        });
        CommonUtil.click(binding.around4Hour, new Action() {
            @Override
            public void click() {
                obtainViewModel().aroundHour.postValue(4);
            }
        });
        CommonUtil.click(binding.around8Hour, new Action() {
            @Override
            public void click() {
                obtainViewModel().aroundHour.postValue(8);
            }
        });
        CommonUtil.click(binding.around12Hour, new Action() {
            @Override
            public void click() {
                obtainViewModel().aroundHour.postValue(12);
            }
        });
        CommonUtil.click(binding.around24Hour, new Action() {
            @Override
            public void click() {
                obtainViewModel().aroundHour.postValue(24);
            }
        });
        CommonUtil.click(binding.aroundSumHour, new Action() {
            @Override
            public void click() {
                showInputDialog(obtainViewModel().hour, new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        obtainViewModel().hour = Integer.parseInt(text);
                        obtainViewModel().aroundHour.postValue(obtainViewModel().hour);
                    }
                });
            }
        });
        CommonUtil.click(binding.aroundConfirm, new Action() {
            @SuppressLint("SetTextI18n")
            @Override
            public void click() {
                int value = obtainViewModel().drawerState.getValue();
                if(value==3){
                    obtainViewModel().drawerState.postValue(1);
                }else{
                    obtainViewModel().drawerState.postValue(2);
                }
                binding.tapAroundText.setText(obtainViewModel().hour+"小时");
            }
        });
    }

    private void showInputDialog(int defaultNumber, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_hour, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);

        etInput.setText(String.valueOf(defaultNumber));
        etInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        etInput.setSelection(String.valueOf(defaultNumber).length());

        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvConfirm.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                listener.onConfirm(input);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            // 设置为屏幕宽度的 85% 可自适应不同机型
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
            window.setAttributes(params);
        }
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
        obtainViewModel().drawerState.observe(this, this::drawerStateChanged);
        obtainViewModel().aroundHour.observe(this, this::aroundHourChanged);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void aroundHourChanged(int aroundHour) {
        if(aroundHour == 4){
            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumText.setText("2小时");
        }
        else if(aroundHour == 8){
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumText.setText("2小时");
        }
        else if(aroundHour == 12){
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumText.setText("2小时");
        }
        else if(aroundHour == 24){
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumText.setText("2小时");
        }
        else{
            binding.aroundSumText.setText(obtainViewModel().hour+"小时");
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
        }
        obtainViewModel().hour = aroundHour;
    }
    private void drawerStateChanged(int drawerState) {
        ///侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
        if(drawerState == 1){
            binding.drawerTitleText.setText("新增巡检");
            binding.llAdd.setVisibility(View.VISIBLE);
            binding.llAround.setVisibility(View.GONE);
            binding.taskStatus.setVisibility(View.GONE);
            binding.taskStatusLine.setVisibility(View.GONE);
        }
        ///侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
        if(drawerState == 3){
            binding.drawerTitleText.setText("新增巡检");
            binding.llAround.setVisibility(View.VISIBLE);
            binding.llAdd.setVisibility(View.GONE);
            binding.taskStatus.setVisibility(View.GONE);
            binding.taskStatusLine.setVisibility(View.GONE);
        }
        ///侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
        if(drawerState == 2){
            binding.drawerTitleText.setText("巡检详情");
            binding.taskStatus.setVisibility(View.VISIBLE);
            binding.taskStatusLine.setVisibility(View.VISIBLE);
            binding.llAdd.setVisibility(View.VISIBLE);
            binding.llAround.setVisibility(View.GONE);
        }
        ///侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
        if(drawerState == 4){
            binding.drawerTitleText.setText("巡检详情");
            binding.taskStatus.setVisibility(View.VISIBLE);
            binding.taskStatusLine.setVisibility(View.VISIBLE);
            binding.llAround.setVisibility(View.VISIBLE);
            binding.llAdd.setVisibility(View.GONE);
        }
    }
    private void nameChanged(String changed) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(Task task) {
        Intent intent = new Intent(this, TaskControlActivity.class);
        intent.putExtra("task",new Gson().toJson(task));
        startActivity(intent);
    }

    @Override
    public void onStartClick(Task task) {
        if(Objects.equals(CommonData.taskId+"", task.getTask_id()+"")){
            ///当前任务已下发
            if(Objects.equals(CommonData.taskStatus, "1")){
                //任务执行中
                Toast.makeText(this, "当前任务正在执行中", Toast.LENGTH_SHORT).show();
            }else{
                //任务未开始
                CommonUtil.showConfirm(this, "确定要开始此任务吗？", "确定", "取消", new Action() {
                    @Override
                    public void click() {
                        obtainViewModel().startTask();
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },false);
            }
        }else{
            ///当前任务未下发
            CommonUtil.showConfirm(this, "确定要下发并开始此任务吗？", "确定", "取消", new Action() {
                @Override
                public void click() {
                    obtainViewModel().postAndStartTaskOnline(task);
                }
            }, new Action() {
                @Override
                public void click() {

                }
            },false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onEditClick(Task task) {
        obtainViewModel().task = task;
        binding.drawerNameEdit.setText(task.getTask_name()+"");
        binding.tapAroundText.setText(CommonUtil.parseCircleShow(task.getTask_timer()));
        int timer = task.getTask_timer()/60/60;
        obtainViewModel().aroundHour.postValue(timer);
        binding.tapStatusText.setText(CommonUtil.parseTaskStatusShow(task.getTaskStatus()));
        binding.tapRouteText.setText(CommonUtil.parseRouteShow(task.getTask_route()));
        obtainViewModel().routeList.clear();
        for (int i = 0; i < task.getTask_route().size(); i++) {
            Task.Route route = task.getTask_route().get(i);
            obtainViewModel().routeList.add(new Point(route.getPoint_index()+"",route.getX(),route.getY(),route.getZ(),"","","","","",route.getLift_param()+"",route.getPoi_name(),route.getPoint_type()+"",route.getLift_param()));
        }
        binding.tapStartTimeText.setText(task.getStart_time()+"");

        obtainViewModel().drawerState.postValue(2);
        binding.warnLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onDeleteClick(Task task) {
        if(Objects.equals(CommonData.taskId+"", task.getTask_id()+"")){
            ///当前任务已下发
            if(Objects.equals(CommonData.taskStatus, "1")){
                //任务执行中
                CommonUtil.showConfirm(this,"当前任务正在进行中，确定结束任务后删除？", "删除", "取消", new Action() {
                    @Override
                    public void click() {
                        obtainViewModel().endTaskAndDelete();
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },true);
            }else{
                ///任务未执行
                CommonUtil.showConfirm(this,"确认删除当前任务吗？", "删除", "取消", new Action() {
                    @Override
                    public void click() {
                        CommonUtil.deleteRobotTask(CommonData.sn,task);
                        obtainViewModel().delayTaskListLocal();
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },true);
            }
        }else{
            ///当前任务未下发
            CommonUtil.showConfirm(this,"确认删除当前任务吗？", "删除", "取消", new Action() {
                @Override
                public void click() {
                    CommonUtil.deleteRobotTask(CommonData.sn,task);
                    obtainViewModel().delayTaskListLocal();
                }
            }, new Action() {
                @Override
                public void click() {

                }
            },true);
        }
    }
}