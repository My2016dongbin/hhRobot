package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPointManageEditBinding;
import com.ehaohai.robot.model.OnStationTypeSelectedListener;
import com.ehaohai.robot.model.PointType;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.viewmodel.PointManageEditViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class PointManageEditActivity extends BaseLiveActivity<ActivityPointManageEditBinding, PointManageEditViewModel>{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        String point = getIntent().getStringExtra("point");
        obtainViewModel().point = new Gson().fromJson(point, new TypeToken<Point>(){}.getType());
        init_();
        bind_();
    }


    @SuppressLint("SetTextI18n")
    private void init_() {
        binding.name.setText(obtainViewModel().point.getName()+"");
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        CommonUtil.click(binding.confirm, new Action() {
            @Override
            public void click() {
                Intent intent = new Intent();
                intent.putExtra("point",new Gson().toJson(obtainViewModel().point));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        CommonUtil.click(binding.tapName, new Action() {
            @Override
            public void click() {
                showNameInputDialog(binding.name.getText().toString(), new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        binding.name.setText(text);
                        obtainViewModel().point.setName(text);
                    }
                });
            }
        });
    }

    private void showNameInputDialog(String defaultName, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("请输入点位名称");

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
    protected ActivityPointManageEditBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_point_manage_edit);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PointManageEditViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PointManageEditViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().picture.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {

    }
}