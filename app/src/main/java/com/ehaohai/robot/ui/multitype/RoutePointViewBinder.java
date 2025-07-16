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
import com.ehaohai.robot.databinding.ItemPointListBinding;
import com.ehaohai.robot.databinding.ItemRoutePointListBinding;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class RoutePointViewBinder extends ItemViewProvider<Point, RoutePointViewBinder.ViewHolder> {
    public Context context;
    public RoutePointViewBinder(Context context) {
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
                R.layout.item_route_point_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Point face) {

        ItemRoutePointListBinding binding = (ItemRoutePointListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, face);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁


        binding.index.setText(face.getIndex()+1+"");
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
    }
}
