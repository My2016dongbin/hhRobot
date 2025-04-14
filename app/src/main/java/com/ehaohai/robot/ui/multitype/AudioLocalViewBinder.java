package com.ehaohai.robot.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.ehaohai.robot.BR;
import com.ehaohai.robot.R;
import com.ehaohai.robot.databinding.ItemAudioLocalListBinding;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.util.InputInfo;
import com.kongzue.dialogx.util.TextInfo;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class AudioLocalViewBinder extends ItemViewProvider<AudioLocal, AudioLocalViewBinder.ViewHolder> {
    public Context context;
    public AudioLocalViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    MediaPlayer player;
    private boolean isPlayingMp3 = false;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_audio_local_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final AudioLocal audio) {

        ItemAudioLocalListBinding binding = (ItemAudioLocalListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, audio);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.name.setText(audio.getName());
        binding.time.setText(audio.getTime());
        CommonUtil.click(binding.play, new Action() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void click() {
                if(isPlayingMp3){
                    binding.play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    stopMp3();
                }else{
                    binding.play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    playMp3(audio.getPath(),binding.play);
                }
            }
        });
        CommonUtil.click(binding.more, new Action() {
            @Override
            public void click() {
                View popupView = LayoutInflater.from(context).inflate(R.layout.popup_item_menu, null);
                PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);

                //设置点击
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //重命名
                popupView.findViewById(R.id.rename).setOnClickListener(view -> {
                    popupWindow.dismiss();
                    TextInfo titleInfo = new TextInfo();
                    titleInfo.setFontColor(context.getResources().getColor(R.color.colorBlack));
                    TextInfo textInfo = new TextInfo();
                    textInfo.setFontColor(context.getResources().getColor(R.color.gray1));
                    InputInfo inputInfo = new InputInfo();
                    inputInfo.setMAX_LENGTH(20);
                    inputInfo.setSelectAllText(true);
                    TextInfo okTextInfo = new TextInfo();
                    okTextInfo.setFontColor(context.getResources().getColor(R.color.text_color_blue));
                    InputDialog.show("重命名文件","","确定","取消",audio.getName())
                            .setButtonOrientation(LinearLayout.VERTICAL)
                            .setOkTextInfo(okTextInfo)
                            .setCancelTextInfo(titleInfo)
                            .setTitleTextInfo(titleInfo)
                            .setInputInfo(inputInfo)
                            .setAutoShowInputKeyboard(false)
                            .setBackgroundColor(context.getResources().getColor(R.color.whiteColor))
                            .setOkButtonClickListener((dialog, v1) -> {
                                String newName = dialog.getInputText().trim();
                                if (!newName.isEmpty()) {
                                    if (renameFile(new File(audio.getPath()), newName)) {
                                        Toast.makeText(context, "文件重命名成功", Toast.LENGTH_SHORT).show();
                                        listener.notifyClick(audio);
                                    }
                                }
                                return false;
                            })
                            .setCancelable(true);
                });

                //删除
                popupView.findViewById(R.id.delete).setOnClickListener(view -> {
                    popupWindow.dismiss();
                    listener.onItemDeleteClick(audio);
                });

                // 显示位置：相对点击的按钮（菜单图标）
                popupWindow.showAsDropDown(binding.more, -70, 30); // 可根据需求微调位置
            }
        });

    }

    public boolean renameFile(File file, String newName) {
        //创建新文件对象，使用相同的父路径和新的文件名
        File newFile = new File(file.getParent(), newName);

        //重命名文件
        return file.renameTo(newFile);  // 如果成功返回 true，否则返回 false
    }

    private void playMp3(String path, ImageView view) {
        if(isPlayingMp3){
            return;
        }
        try {
            LibVLC libVLC = new LibVLC(context);
            player = new MediaPlayer(libVLC);
            player.play(path);
            isPlayingMp3 = true;

            player.setEventListener(new MediaPlayer.EventListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    if(event.type == MediaPlayer.Event.EndReached){
                        view.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                        isPlayingMp3 = false;
                        player.release();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopMp3() {
        player.stop();
        isPlayingMp3 = false;
        player.release();
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


    public void onItemClick(AudioLocal audio){
        listener.onItemClick(audio);
    }

    public interface OnItemClickListener{
        void onItemClick(AudioLocal audio);
        void onItemDeleteClick(AudioLocal audio);
        void notifyClick(AudioLocal audio);
    }
}
