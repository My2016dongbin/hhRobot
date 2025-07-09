package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemTaskListBinding;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TaskViewBinder extends ItemViewProvider<Task, TaskViewBinder.ViewHolder> {
    public Context context;
    public TaskViewBinder(Context context) {
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
                R.layout.item_task_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Task task) {

        ItemTaskListBinding binding = (ItemTaskListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, task);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.taskName.setText(task.getTaskName());
        binding.taskCircle.setText(task.getTaskCircle());
        binding.taskRoute.setText(task.getTaskRoute());
        binding.taskStatus.setText(task.getTaskStatus());
        binding.taskStartTime.setText(task.getTaskStartTime());
        CommonUtil.click(binding.start, new Action() {
            @Override
            public void click() {
                listener.onStartClick(task);
            }
        });
        CommonUtil.click(binding.edit, new Action() {
            @Override
            public void click() {
                listener.onEditClick(task);
            }
        });
        CommonUtil.click(binding.delete, new Action() {
            @Override
            public void click() {
                listener.onDeleteClick(task);
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


    public void onItemClick(Task task){
        listener.onItemClick(task);
    }

    public interface OnItemClickListener{
        void onItemClick(Task task);
        void onStartClick(Task task);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }
}
