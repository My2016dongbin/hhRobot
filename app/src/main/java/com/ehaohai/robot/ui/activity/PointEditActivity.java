package com.ehaohai.robot.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPointEditBinding;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.multitype.PointViewBinder;
import com.ehaohai.robot.ui.multitype.Task;
import com.ehaohai.robot.ui.viewmodel.PointEditViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class PointEditActivity extends BaseLiveActivity<ActivityPointEditBinding, PointEditViewModel> implements PointViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        String point = getIntent().getStringExtra("point");
        obtainViewModel().point = new Gson().fromJson(point, new TypeToken<Point>(){}.getType());
        init_();
        bind_();
    }


    private void init_() {

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
        CommonUtil.click(binding.tapName, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.tapType, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.tapFloor, new Action() {
            @Override
            public void click() {

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
    protected ActivityPointEditBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_point_edit);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PointEditViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PointEditViewModel.class);
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
}