package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPictureListBinding;
import com.ehaohai.robot.databinding.ActivityPointListBinding;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Face;
import com.ehaohai.robot.ui.multitype.FaceViewBinder;
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.PictureViewBinder;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.PointViewBinder;
import com.ehaohai.robot.ui.viewmodel.PictureListViewModel;
import com.ehaohai.robot.ui.viewmodel.PointListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

;

public class PointListActivity extends BaseLiveActivity<ActivityPointListBinding, PointListViewModel> implements PointViewBinder.OnItemClickListener {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {
        ///处理页面返回回参
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 在这里处理返回的数据
                        Intent data = result.getData();
                        String json = data.getStringExtra("point");
                        Point editPoint = new Gson().fromJson(json, new TypeToken<Point>(){}.getType());
                        obtainViewModel().editReplace(editPoint);
                    }
                }
        );
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
                obtainViewModel().postList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        PointViewBinder pointViewBinder = new PointViewBinder(this);
        pointViewBinder.setListener(this);
        obtainViewModel().adapter.register(Point.class, pointViewBinder);
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        obtainViewModel().postList();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        CommonUtil.click(binding.confirm, new Action() {
            @Override
            public void click() {
                finish();
            }
        });
    }

    @Override
    protected ActivityPointListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_point_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PointListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PointListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().picture.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {

    }

    @Override
    public void onPointClick(Point point) {
        EventBus.getDefault().post(point);
        finish();
    }

    @Override
    public void onPointEditClick(Point face) {
        Intent intent = new Intent(this, PointEditActivity.class);
        intent.putExtra("point",new Gson().toJson(face));
        activityResultLauncher.launch(intent);
    }
}