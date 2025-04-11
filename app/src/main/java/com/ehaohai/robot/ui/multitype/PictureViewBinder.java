package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemPictureListBinding;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PictureViewBinder extends ItemViewProvider<Picture, PictureViewBinder.ViewHolder> {
    public Context context;
    public PictureViewBinder(Context context) {
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
                R.layout.item_picture_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Picture picture) {

        ItemPictureListBinding binding = (ItemPictureListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, picture);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        Glide.with(context).load(picture.getUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                .error(R.drawable.ic_no_pic).into(binding.picture);
        if(picture.isShowChoose()){
            binding.select.setVisibility(View.VISIBLE);
        }else{
            binding.select.setVisibility(View.GONE);
        }
        if(picture.isSelected()){
            binding.select.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_yes));
        }else{
            binding.select.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_un));
        }
        CommonUtil.click(binding.picture, new Action() {
            @Override
            public void click() {
                listener.onItemClick(picture);
            }
        });
        CommonUtil.click(binding.select, new Action() {
            @Override
            public void click() {
                listener.onItemSelected(picture);
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


    public void onItemClick(Picture taskList){
        listener.onItemClick(taskList);
    }

    public interface OnItemClickListener{
        void onItemClick(Picture taskList);
        void onItemSelected(Picture taskList);
    }
}
