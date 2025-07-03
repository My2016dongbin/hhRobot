package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPictureListBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.PictureViewBinder;
import com.ehaohai.robot.ui.viewmodel.PictureListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

;import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class PictureListActivity extends BaseLiveActivity<ActivityPictureListBinding, PictureListViewModel> implements PictureViewBinder.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///图片列表刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(PictureRefresh event) {
        obtainViewModel().postPictureList();
    }

    private void init_() {
        ///图片列表
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(gridLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.refresh.setRefreshHeader(new ClassicsHeader(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //取消
                disChoose();

                obtainViewModel().postPictureList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        PictureViewBinder pictureViewBinder = new PictureViewBinder(this);
        pictureViewBinder.setListener(this);
        obtainViewModel().adapter.register(Picture.class, pictureViewBinder);
        //obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        obtainViewModel().postPictureList();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        CommonUtil.click(binding.state, new Action() {
            @Override
            public void click() {
                obtainViewModel().state = !obtainViewModel().state;
                if(obtainViewModel().state){
                    //选择
                    choose();
                }else{
                    //取消
                    disChoose();
                }
            }
        });
        CommonUtil.click(binding.delete, new Action() {
            @Override
            public void click() {
                List<Picture> list = new ArrayList<>();
                for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
                    Picture picture = obtainViewModel().pictureList.get(i);
                    if(picture.isSelected()){
                        list.add(picture);
                    }
                }
                if(list.isEmpty()){
                    Toast.makeText(PictureListActivity.this, "您还没有选择图片", Toast.LENGTH_SHORT).show();
                }else{
                    CommonUtil.showConfirm(PictureListActivity.this, "确认删除选中的图片吗？", "删除", "取消", new Action() {
                        @Override
                        public void click() {
                            for (int i = 0; i < list.size(); i++) {
                                Picture picture = list.get(i);
                                CommonUtil.deleteFile(picture.getPath());
                            }
                            Toast.makeText(PictureListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new PictureRefresh());
                        }
                    }, new Action() {
                        @Override
                        public void click() {

                        }
                    },true);
                }
            }
        });
        CommonUtil.click(binding.download, new Action() {
            @Override
            public void click() {
                List<Picture> list = new ArrayList<>();
                for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
                    Picture picture = obtainViewModel().pictureList.get(i);
                    if(picture.isSelected()){
                        list.add(picture);
                    }
                }
                if(list.isEmpty()){
                    Toast.makeText(PictureListActivity.this, "您还没有选择图片", Toast.LENGTH_SHORT).show();
                }else{
                    for (int i = 0; i < list.size(); i++) {
                        Picture picture = list.get(i);
                        CommonUtil.downLoadFile(PictureListActivity.this,picture.getPath());
                    }
                    Toast.makeText(PictureListActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disChoose() {
        binding.state.setText("选择");
        changePictureState(false);
        binding.delete.setVisibility(View.GONE);
        binding.download.setVisibility(View.GONE);
    }

    private void choose() {
        binding.state.setText("取消");
        changePictureState(true);
        binding.delete.setVisibility(View.VISIBLE);
        binding.download.setVisibility(View.VISIBLE);
    }

    private void changePictureState(boolean state) {
        for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
            Picture picture = obtainViewModel().pictureList.get(i);
            picture.setShowChoose(state);
        }
        obtainViewModel().updateData();
    }

    @Override
    protected ActivityPictureListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_picture_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PictureListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PictureListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().picture.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(Picture pic) {
        ArrayList<String> picUrls = new ArrayList<>();
        if(obtainViewModel().state){
            //-多图片预览
            ///选择模式-点击未选图片
            if(!pic.isSelected()){
                picUrls.add(pic.getPath());
                Intent intent = new Intent(this, PictureViewerActivity.class);
                intent.putStringArrayListExtra("urls", picUrls);
                intent.putExtra("delete",true);
                startActivity(intent);
                return;
            }
            ///选择模式-点击已选图片
            Intent intent = new Intent(this, PictureViewerActivity.class);
            for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
                Picture picture = obtainViewModel().pictureList.get(i);
                if(picture.isSelected()){
                    picUrls.add(picture.getPath());
                }
            }
            //已选0张图片
            if(picUrls.isEmpty()){
                picUrls.add(pic.getPath());
            }

            int index = 0;
            for (int m = 0; m < picUrls.size(); m++) {
                String url = picUrls.get(m);
                if(Objects.equals(url, pic.getPath())){
                    index = m;
                    break;
                }
            }
            intent.putStringArrayListExtra("urls", picUrls);
            intent.putExtra("index",index);
            intent.putExtra("delete",true);
            startActivity(intent);
        }else{
            //-单图片预览
            Intent intent = new Intent(this, PictureViewerActivity.class);
            picUrls.add(pic.getPath());
            intent.putStringArrayListExtra("urls", picUrls);
            intent.putExtra("delete",true);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(Picture pic) {
        for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
            Picture picture = obtainViewModel().pictureList.get(i);
            if(Objects.equals(picture.getId(), pic.getId())){
                picture.setSelected(!picture.isSelected());
                obtainViewModel().updateDataSelected(pic);
                return;
            }
        }
    }
}