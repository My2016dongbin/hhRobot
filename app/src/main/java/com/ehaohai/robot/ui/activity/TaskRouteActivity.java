package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityTaskRouteBinding;
import com.ehaohai.robot.event.TaskRoute;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.AddRoute;
import com.ehaohai.robot.ui.multitype.AddRouteViewBinder;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.RoutePointViewBinder;
import com.ehaohai.robot.ui.viewmodel.TaskRouteViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.drakeet.multitype.MultiTypeAdapter;

public class TaskRouteActivity extends BaseLiveActivity<ActivityTaskRouteBinding, TaskRouteViewModel> implements RoutePointViewBinder.OnItemClickListener, AddRouteViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///接收选点数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Point point) {
        obtainViewModel().pointList.add(point);
        obtainViewModel().updateData();
    }

    private void init_() {
        //获取上个页面已选的点位
        if(!CommonData.routeList.isEmpty()){
            obtainViewModel().pointList = CommonData.routeList;
        }

        ///任务点列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.refresh.setRefreshHeader(new ClassicsHeader(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().updateData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        RoutePointViewBinder pointViewBinder = new RoutePointViewBinder(this);
        pointViewBinder.setListener(this);
        obtainViewModel().adapter.register(Point.class, pointViewBinder);
        AddRouteViewBinder addRouteViewBinder = new AddRouteViewBinder(this);
        addRouteViewBinder.setListener(this);
        obtainViewModel().adapter.register(AddRoute.class, addRouteViewBinder);
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        obtainViewModel().updateData();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        CommonUtil.click(binding.confirm, new Action() {
            @Override
            public void click() {
                if(obtainViewModel().pointList.size()>0){
                    ///Eventbus发送
                    EventBus.getDefault().post(new TaskRoute(obtainViewModel().pointList));
                    finish();
                }else{
                    Toast.makeText(TaskRouteActivity.this, "请先添加任务路径", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showInputDialog(String defaultName, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);

        etInput.setText(defaultName);
        etInput.setSelection(defaultName.length());

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
    protected ActivityTaskRouteBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_task_route);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TaskRouteViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TaskRouteViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().picture.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPointClick(Point face) {

    }
    @Override
    public void onPointDeleteClick(Point face) {
        obtainViewModel().pointList.remove(face);
        obtainViewModel().updateData();
    }

    @Override
    public void onAddRouteClick() {
        TaskRouteActivity.this.startActivity(new Intent(TaskRouteActivity.this,PointListActivity.class));
    }
}