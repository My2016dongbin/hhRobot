package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemSingleWarnListBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SingleWarnViewBinder extends ItemViewProvider<Warn, SingleWarnViewBinder.ViewHolder> {
    public Context context;
    public SingleWarnViewBinder(Context context) {
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
                R.layout.item_single_warn_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Warn warn) {

        ItemSingleWarnListBinding binding = (ItemSingleWarnListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, warn);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.findTime.setText(warn.getFindTime());
        binding.findType.setText(warn.getFindType());
        binding.findResult.setText(warn.getFindResult());
        if(warn.getCount()%2==0){
            binding.click.setBackgroundResource(R.color.colorBlackBack2);
        }else{
            binding.click.setBackgroundResource(R.color.gray_back);
        }
        if(warn.isUnRead()){
            binding.unread.setVisibility(View.VISIBLE);
        }else{
            binding.unread.setVisibility(View.GONE);
        }
        Glide.with(context).load(warn.getFindImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .error(R.drawable.ic_no_pic).into(binding.findImage);

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


    public void onItemClick(Warn taskList){
        listener.onItemClick(taskList);
    }

    public interface OnItemClickListener{
        void onItemClick(Warn taskList);
    }
}
