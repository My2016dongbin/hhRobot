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
import com.ehaohai.robot.databinding.ActivityPointEditBinding;
import com.ehaohai.robot.model.OnStationTypeSelectedListener;
import com.ehaohai.robot.model.PointType;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.ui.viewmodel.PointEditViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class PointEditActivity extends BaseLiveActivity<ActivityPointEditBinding, PointEditViewModel>{
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
        binding.type.setText(CommonUtil.parsePointTypeByCode(obtainViewModel().point.getType()+""));
        binding.floor.setText(obtainViewModel().point.getTaskFloor()+"");
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
        CommonUtil.click(binding.tapType, new Action() {
            @Override
            public void click() {
                showStationTypePicker(PointEditActivity.this, binding.type.getText().toString(),new OnStationTypeSelectedListener() {
                    @Override
                    public void onSelected(String typeName) {
                        binding.type.setText(typeName);
                        obtainViewModel().point.setType(CommonUtil.parsePointTypeByName(typeName));
                    }
                });
            }
        });
        CommonUtil.click(binding.tapFloor, new Action() {
            @Override
            public void click() {
                showFloorInputDialog(binding.floor.getText().toString(), new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        binding.floor.setText(text);
                        obtainViewModel().point.setTaskFloor(Integer.parseInt(text));
                    }
                });
            }
        });
    }
    private void showStationTypePicker(Context context, String type,OnStationTypeSelectedListener listener) {
        List<PointType> pointTypeList = CommonUtil.getPointTypeList();
        List<String> stationTypes = new ArrayList<>();
        for (int i = 0; i < pointTypeList.size(); i++) {
            stationTypes.add(pointTypeList.get(i).getName());
        }

        OptionsPickerView<String> pickerView = new OptionsPickerBuilder(context, (options1, option2, options3, v) -> {
            // 回调选择结果
            String name = stationTypes.get(options1);
            listener.onSelected(name);
        })
                .setTitleText("选择点位类型")
                .setContentTextSize(18)
                .setOutSideCancelable(true)
                .setBgColor(getResources().getColor(R.color.setting))
                .setTitleBgColor(getResources().getColor(R.color.setting))
                .setTitleColor(Color.WHITE)
                .setCancelColor(Color.LTGRAY)
                .setSubmitColor(Color.WHITE)
                .setTextColorCenter(Color.WHITE)
                .isCenterLabel(true)
                .setLineSpacingMultiplier(2.0f)
                .build();

        pickerView.setPicker(stationTypes);
        pickerView.setSelectOptions(CommonUtil.parsePointTypeIndexByName(type));
        pickerView.show();
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
    private void showFloorInputDialog(String defaultName, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("请输入楼层");
        etInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        etInput.setText(defaultName);
        etInput.setSelection(defaultName.length());

        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvConfirm.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                listener.onConfirm(input);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "楼层不能为空", Toast.LENGTH_SHORT).show();
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
}