package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.multitype.WarnBugViewBinder;
import com.ehaohai.robot.ui.multitype.TaskViewBinder;
import com.ehaohai.robot.ui.viewmodel.TaskListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
        CommonUtil.click(binding.add, new Action() {
            @Override
            public void click() {
                binding.warnLayout.openDrawer(GravityCompat.END);
            }
        });
        CommonUtil.click(binding.addConfirm, new Action() {
            @Override
            public void click() {
                binding.warnLayout.closeDrawer(GravityCompat.END);
            }
        });
        CommonUtil.click(binding.tapLines, new Action() {
            @Override
            public void click() {
                ///任务路径选择
                startActivity(new Intent(TaskListActivity.this,TaskRouteActivity.class));
            }
        });
        CommonUtil.click(binding.tapStartTime, new Action() {
            @Override
            public void click() {
                TimePickerView pvTime = new TimePickerBuilder(TaskListActivity.this, (date, v) -> {
                    String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date);
                    binding.startTimeText.setText(time);
                })
                        .setType(new boolean[]{false, false, false, true, true, true})
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
                obtainViewModel().drawerState.postValue(3);
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
                obtainViewModel().drawerState.postValue(1);
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
        if(aroundHour == 4 && obtainViewModel().hour!=4){
            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
        }
        else if(aroundHour == 8 && obtainViewModel().hour!=8){
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
        }
        else if(aroundHour == 12 && obtainViewModel().hour!=12){
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
        }
        else if(aroundHour == 24 && obtainViewModel().hour!=24){
            binding.around24HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_yes));

            binding.around4HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around8HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.around12HourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
            binding.aroundSumHourState.setImageDrawable(getResources().getDrawable(R.drawable.ic_un));
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
            binding.llAdd.setVisibility(View.VISIBLE);
            binding.llAround.setVisibility(View.GONE);
        }
        ///侧边栏页面状态  1：新增巡检；3：新增-选择任务周期; ... || 2：编辑-巡检详情；4：编辑-选择任务周期; ...
        if(drawerState == 3){
            binding.llAround.setVisibility(View.VISIBLE);
            binding.llAdd.setVisibility(View.GONE);
        }
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