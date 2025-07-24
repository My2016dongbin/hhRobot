package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.ehaohai.robot.databinding.ItemWarnListBinding;
import com.ehaohai.robot.ui.activity.PictureViewerActivity;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class WarnViewBinder extends ItemViewProvider<Warn, WarnViewBinder.ViewHolder> {
    public Context context;
    public WarnViewBinder(Context context) {
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
                R.layout.item_warn_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Warn warn) {

        ItemWarnListBinding binding = (ItemWarnListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, warn);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.findTime.setText(warn.getTimeStamp());
        binding.deviceType.setText(warn.getDeviceType());
        binding.deviceName.setText(warn.getDeviceName());
        binding.deviceType.setText("机器狗");
        binding.deviceName.setText("浩海机器狗");
        if(warn.getMoreInfo()!=null){
            binding.findType.setText(warn.getMoreInfo().getName());
            binding.findResult.setText(warn.getMoreInfo().getAlarmInfo());
        }
        if(warn.getCount()%2==0){
            binding.click.setBackgroundResource(R.color.colorBlackBack2);
        }else{
            binding.click.setBackgroundResource(R.color.gray_back);
        }
        if(Objects.equals(warn.getIsRead(), "0")){
            binding.unread.setVisibility(View.VISIBLE);
        }else{
            binding.unread.setVisibility(View.GONE);
        }
        Glide.with(context).load(warn.getImgPath())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .error(R.drawable.error_pic).into(binding.findImage);
        CommonUtil.click(binding.findImage, new Action() {
            @Override
            public void click() {
                ArrayList<String> picUrls = new ArrayList<>();
                picUrls.add(warn.getImgPath());
                Intent intent = new Intent(context, PictureViewerActivity.class);
                intent.putStringArrayListExtra("urls", picUrls);
                context.startActivity(intent);
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


    public void onItemClick(Warn taskList){
        listener.onItemClick(taskList);
    }

    public interface OnItemClickListener{
        void onItemClick(Warn taskList);
    }
}
