package com.ehaohai.robot.ui.multitype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemEmptyNullTextBinding;
import com.ehaohai.robot.databinding.ItemEmptyNullTextBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class EmptyNullViewBinder extends ItemViewProvider<Empty, EmptyNullViewBinder.ViewHolder> {
    public Context context;
    public EmptyNullViewBinder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_empty_null_text, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Empty empty) {

        ItemEmptyNullTextBinding binding = (ItemEmptyNullTextBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, empty);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁
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

    public void onItemClick(Empty empty){

    }

}
