package com.ehaohai.robot.ui.activity;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPictureViewerBinding;
import com.ehaohai.robot.event.DeviceRefresh;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.ui.multitype.FacePicture;
import com.ehaohai.robot.ui.viewmodel.PictureViewerViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

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
        obtainViewModel().canDelete = intent.getBooleanExtra("delete",false);
        obtainViewModel().online = intent.getBooleanExtra("online",false);
        if(obtainViewModel().online){
            String onlineList = intent.getStringExtra("onlineList");
            obtainViewModel().facePictureList = new Gson().fromJson(onlineList, new TypeToken<List<FacePicture>>(){}.getType());
        }
        //是否显示删除按钮
        if(obtainViewModel().canDelete){
            binding.delete.setVisibility(View.VISIBLE);
        }else{
            binding.delete.setVisibility(View.GONE);
        }
        if(!obtainViewModel().urls.isEmpty()){
            showPicAndIndex();
        }else{
            Toast.makeText(this, "没有图片展示", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPicAndIndex() {
        HhLog.e("show " + obtainViewModel().urls.get(obtainViewModel().pictureIndex));
        String path = obtainViewModel().urls.get(obtainViewModel().pictureIndex);

        if(obtainViewModel().online){
            //在线图片
            binding.mBigImage.setVisibility(View.VISIBLE);
            binding.videoView.setVisibility(View.GONE);
            binding.mBigImage.showImage(Uri.parse(path));
        }else{
            if(path.endsWith("mp4")){
                //本地mp4
                binding.videoView.setVisibility(View.VISIBLE);
                binding.mBigImage.setVisibility(View.GONE);

                ExoPlayer player = new ExoPlayer.Builder(this).build();
                binding.videoView.setPlayer(player);

                MediaItem mediaItem = MediaItem.fromUri(path);
                player.setMediaItem(mediaItem);
                player.prepare();

            }else{
                //本地图片
                binding.mBigImage.setVisibility(View.VISIBLE);
                binding.videoView.setVisibility(View.GONE);
                binding.mBigImage.showImage(Uri.fromFile(new File(path)));
            }
        }
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
                if(obtainViewModel().online){
                    //CommonUtil.downloadImageToGallery(PictureViewerActivity.this,"https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAOEcdM.img");
                    CommonUtil.downloadImageToGallery(PictureViewerActivity.this,obtainViewModel().urls.get(obtainViewModel().pictureIndex));
                }else{
                    CommonUtil.downLoadFile(PictureViewerActivity.this,obtainViewModel().urls.get(obtainViewModel().pictureIndex));
                    Toast.makeText(PictureViewerActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///删除
        CommonUtil.click(binding.delete, new Action() {
            @Override
            public void click() {
                CommonUtil.showConfirm(PictureViewerActivity.this, "确认删除该图片吗？", "删除", "取消", new Action() {
                    @Override
                    public void click() {
                        if(obtainViewModel().online){
                            obtainViewModel().deleteOnline(new Action() {
                                @Override
                                public void click() {
                                    finish();
                                }
                            });
                        }else{
                            CommonUtil.deleteFile(obtainViewModel().urls.get(obtainViewModel().pictureIndex));
                            Toast.makeText(PictureViewerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            refreshUrls();
                            EventBus.getDefault().post(new PictureRefresh());
                        }
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

    private void refreshUrls() {
        obtainViewModel().urls.remove(obtainViewModel().pictureIndex);
        if(obtainViewModel().urls.size()==0){
            finish();
        }
        else{
            obtainViewModel().pictureIndex = 0;
            showPicAndIndex();
        }
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