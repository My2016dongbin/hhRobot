package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemPointListBinding;
import com.ehaohai.robot.ui.activity.PointEditActivity;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.google.gson.Gson;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.util.InputInfo;
import com.kongzue.dialogx.util.TextInfo;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PointViewBinder extends ItemViewProvider<Point, PointViewBinder.ViewHolder> {
    public Context context;
    public PointViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_point_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Point face) {

        ItemPointListBinding binding = (ItemPointListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, face);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁


        binding.name.setText(face.getName()+"");
        binding.xy.setText(face.getX()+","+face.getY());
        binding.type.setText(CommonUtil.parsePointTypeByCode(face.getType()+""));
        binding.floor.setText(face.getTaskFloor()+"");
        CommonUtil.click(binding.click, new Action() {
            @Override
            public void click() {
                listener.onPointClick(face);
            }
        });
        CommonUtil.click(binding.more, new Action() {
            @Override
            public void click() {
                View popupView = LayoutInflater.from(context).inflate(R.layout.popup_item_menu_point, null);
                PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);

                //设置点击
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //点位修改
                popupView.findViewById(R.id.edit).setOnClickListener(view -> {
                    popupWindow.dismiss();
                    listener.onPointEditClick(face);
                });

                // 显示位置：相对点击的按钮（菜单图标）
                popupWindow.showAsDropDown(binding.more, -10, 5); // 可根据需求微调位置
            }
        });

    }

    static class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private final B mBinding;

        ViewHolder(B mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
        public B getBinding() {
            return mBinding;
        }
    }


    public interface OnItemClickListener{
        void onPointClick(Point face);
        void onPointEditClick(Point face);
    }
}
