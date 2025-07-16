package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPointManageBinding;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.PointViewBinder;
import com.ehaohai.robot.ui.viewmodel.PointManageViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import org.greenrobot.eventbus.EventBus;
import me.drakeet.multitype.MultiTypeAdapter;


public class PointManageActivity extends BaseLiveActivity<ActivityPointManageBinding, PointManageViewModel> implements PointViewBinder.OnItemClickListener {
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
        CommonUtil.click(binding.map, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(PointManageActivity.this,MapModeActivity.class));
            }
        });
        CommonUtil.click(binding.point, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(PointManageActivity.this,PointModeActivity.class));
            }
        });
    }

    @Override
    protected ActivityPointManageBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_point_manage);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PointManageViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PointManageViewModel.class);
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
        Intent intent = new Intent(this, PointManageEditActivity.class);
        intent.putExtra("point",new Gson().toJson(face));
        activityResultLauncher.launch(intent);
    }
}