package com.ehaohai.robot.ui.activity;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPictureViewerBinding;
import com.ehaohai.robot.event.DeviceRefresh;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.viewmodel.PictureViewerViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class PictureViewerActivity extends BaseLiveActivity<ActivityPictureViewerBinding, PictureViewerViewModel> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    private void init_() {
        Intent intent = getIntent();
        obtainViewModel().urls = intent.getStringArrayListExtra("urls");
        obtainViewModel().pictureIndex = intent.getIntExtra("index",0);
        if(!obtainViewModel().urls.isEmpty()){
            showPicAndIndex();
        }else{
            Toast.makeText(this, "没有图片展示", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPicAndIndex() {
        binding.mBigImage.showImage(Uri.parse(obtainViewModel().urls.get(obtainViewModel().pictureIndex)));
        binding.number.setText(obtainViewModel().pictureIndex+1+"/"+obtainViewModel().urls.size());
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        ///下载
        CommonUtil.click(binding.download, new Action() {
            @Override
            public void click() {
                Toast.makeText(PictureViewerActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            }
        });
        ///删除
        CommonUtil.click(binding.delete, new Action() {
            @Override
            public void click() {
                CommonUtil.showConfirm(PictureViewerActivity.this, "确定要删除该图片吗？", "删除", "取消", new Action() {
                    @Override
                    public void click() {
                        Toast.makeText(PictureViewerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new PictureRefresh());
                    }
                }, new Action() {
                    @Override
                    public void click() {

                    }
                },true);
            }
        });
        ///上一张
        CommonUtil.click(binding.left, new Action() {
            @Override
            public void click() {
                obtainViewModel().pictureIndex--;
                if(obtainViewModel().pictureIndex<0){
                    obtainViewModel().pictureIndex = 0;
                    Toast.makeText(PictureViewerActivity.this, "当前已经是第一张了", Toast.LENGTH_SHORT).show();
                }else{
                    showPicAndIndex();
                }
            }
        });
        ///下一张
        CommonUtil.click(binding.right, new Action() {
            @Override
            public void click() {
                obtainViewModel().pictureIndex++;
                if(obtainViewModel().pictureIndex>obtainViewModel().urls.size()-1){
                    obtainViewModel().pictureIndex = obtainViewModel().urls.size()-1;
                    Toast.makeText(PictureViewerActivity.this, "当前已经是最后一张了", Toast.LENGTH_SHORT).show();
                }else{
                    showPicAndIndex();
                }
            }
        });
    }

    @Override
    protected ActivityPictureViewerBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_picture_viewer);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PictureViewerViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PictureViewerViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().message.observe(this, this::messageChanged);
    }

    private void messageChanged(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}