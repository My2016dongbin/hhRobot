package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityPictureFaceListBinding;
import com.ehaohai.robot.event.PictureRefresh;
import com.ehaohai.robot.ui.cell.OnInputConfirmListener;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyNullViewBinder;
import com.ehaohai.robot.ui.multitype.FacePicture;
import com.ehaohai.robot.ui.multitype.FacePictureViewBinder;
import com.ehaohai.robot.ui.viewmodel.PictureFaceListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class PictureFaceListActivity extends BaseLiveActivity<ActivityPictureFaceListBinding, PictureFaceListViewModel> implements FacePictureViewBinder.OnItemClickListener{
    private static final int REQUEST_CODE_TAKE_PHOTO = 1001;
    private static final int REQUEST_CODE_PICK_PHOTO = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        obtainViewModel().fileName = getIntent().getStringExtra("fileName");
        init_();
        bind_();
    }

    ///图片列表刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(PictureRefresh event) {
        obtainViewModel().postFaceList();
    }

    private void init_() {
        binding.title.setText(obtainViewModel().fileName);
        ///图片列表
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4,LinearLayoutManager.VERTICAL,false);
        binding.recyclePic.setLayoutManager(gridLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recyclePic.setHasFixedSize(true);
        binding.recyclePic.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.refreshPic.setRefreshHeader(new ClassicsHeader(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refreshPic.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //取消
                disChoose();
                obtainViewModel().state = false;

                obtainViewModel().postFaceList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        FacePictureViewBinder facePictureViewBinder = new FacePictureViewBinder(this);
        facePictureViewBinder.setListener(this);
        obtainViewModel().adapter.register(FacePicture.class, facePictureViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyNullViewBinder(this));
        binding.recyclePic.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recyclePic, obtainViewModel().adapter);

        obtainViewModel().postFaceList();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
        CommonUtil.click(binding.add, new Action() {
            @Override
            public void click() {
                if(obtainViewModel().pictureList.size()<3){
                    showChooseDialog();
                }else{
                    Toast.makeText(PictureFaceListActivity.this, "最多上传三张人脸图片", Toast.LENGTH_SHORT).show();
                }
            }
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
                List<FacePicture> list = new ArrayList<>();
                for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
                    FacePicture picture = obtainViewModel().pictureList.get(i);
                    if(picture.isSelected()){
                        list.add(picture);
                    }
                }
                if(list.isEmpty()){
                    Toast.makeText(PictureFaceListActivity.this, "您还没有选择图片", Toast.LENGTH_SHORT).show();
                }else{
                    if(list.size()>=obtainViewModel().pictureList.size()){
                        //删除全部照片-并且删除整个文件夹
                        CommonUtil.showConfirm(PictureFaceListActivity.this, "您选择了所有人脸图片，确认要删除整个文件夹吗？", "删除", "取消", new Action() {
                            @Override
                            public void click() {
                                obtainViewModel().deleteFile(new Action() {
                                    @Override
                                    public void click() {
                                        finish();
                                    }
                                });
                            }
                        }, new Action() {
                            @Override
                            public void click() {

                            }
                        },true);
                    }else{
                        //删除部分照片
                        CommonUtil.showConfirm(PictureFaceListActivity.this, "确认删除选中的图片吗？", "删除", "取消", new Action() {
                            @Override
                            public void click() {
                                obtainViewModel().deletePicture(list, new Action() {
                                    @Override
                                    public void click() {
                                        //取消
                                        disChoose();
                                        obtainViewModel().state = false;
                                    }
                                });
                            }
                        }, new Action() {
                            @Override
                            public void click() {

                            }
                        },true);
                    }
                }
            }
        });
    }

    private void showChooseDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.choose_dialog, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
        TextView tvTakePhoto = view.findViewById(R.id.tv_take_photo);
        TextView tvAlbum = view.findViewById(R.id.tv_album);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        CommonUtil.click(tvTakePhoto, () -> {
            dialog.dismiss();
            takePhoto();
        });
        CommonUtil.click(tvAlbum, () -> {
            dialog.dismiss();
            openGallery();
        });
        CommonUtil.click(tvCancel, dialog::dismiss);
        dialog.show();
    }

    private void takePhoto() {
        HhLog.e("takePhoto");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                String fileName = "face_" + System.currentTimeMillis() + ".png";
                photoFile = new File(getCacheDir(), fileName);
                if (!photoFile.exists()) {
                    photoFile.createNewFile();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            /*if (photoFile != null) {
                obtainViewModel().facePath = photoFile.getPath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, obtainViewModel().facePath);
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            }*/

            if (photoFile != null) {
                obtainViewModel().facePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider", // 与 Manifest 里的 authorities 对应
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // 重要：授予相机读写权限
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            }
        }else{
            // ------- 鸿蒙等不支持 ACTION_IMAGE_CAPTURE 的机型，走内置相机 -------
            openCameraX();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                if (obtainViewModel().facePath != null) {
                    String imagePath = obtainViewModel().facePath;
                    HhLog.e("ImagePath", "Selected image path: REQUEST_CODE_TAKE_PHOTO " + imagePath);
                    /*showInputDialog("", text -> {
                        obtainViewModel().createFaceFile(text,imagePath);
                    });*/
                    obtainViewModel().createFaceFile(obtainViewModel().fileName, imagePath);
                }
            } else if (requestCode == REQUEST_CODE_PICK_PHOTO) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    String fileName = "face_" + System.currentTimeMillis() + ".png";
                    File photoFile = new File(getCacheDir(), fileName);

                    if (selectedImageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            if (inputStream != null) {
                                FileOutputStream outputStream = new FileOutputStream(photoFile);

                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }

                                outputStream.flush();
                                outputStream.close();
                                inputStream.close();


                                String imagePath = photoFile.getPath();
                                HhLog.e("ImagePath", "Selected image path: REQUEST_CODE_PICK_PHOTO " + imagePath);
                                /*showInputDialog("", text -> {
                                    obtainViewModel().createFaceFile(text,imagePath);
                                });*/
                                obtainViewModel().createFaceFile(obtainViewModel().fileName, imagePath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }else if (requestCode == 2001) {
                String imagePath = data.getStringExtra(CameraXActivity.EXTRA_RESULT_PATH);
                if (imagePath != null) {
                    HhLog.e("ImagePath", "Selected image path: REQUEST_CODE_CAMERAX " + imagePath);
                    showInputDialog("", text -> {
                        obtainViewModel().createFaceFile(text, imagePath);
                    });
                }
            }

        }
    }

    private void openCameraX() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent = new Intent(this, CameraXActivity.class);
        }
        startActivityForResult(intent, 2001);
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    private void showInputDialog(String defaultName, OnInputConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog2);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText etInput = view.findViewById(R.id.et_input);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);

        etInput.setText(defaultName);
        etInput.setSelection(defaultName.length());

        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvConfirm.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                listener.onConfirm(input);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            // 设置为屏幕宽度的 85% 可自适应不同机型
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
            window.setAttributes(params);
        }
    }



    private void disChoose() {
        binding.state.setText("选择");
        changePictureState(false);
        binding.delete.setVisibility(View.GONE);
    }

    private void choose() {
        binding.state.setText("取消");
        changePictureState(true);
        binding.delete.setVisibility(View.VISIBLE);
    }

    private void changePictureState(boolean state) {
        for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
            FacePicture picture = obtainViewModel().pictureList.get(i);
            picture.setShowChoose(state);
        }
        obtainViewModel().updateDataPic();
    }

    @Override
    protected ActivityPictureFaceListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_picture_face_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public PictureFaceListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(PictureFaceListViewModel.class);
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
    public void onItemClick(FacePicture pic) {
        ArrayList<String> picUrls = new ArrayList<>();
        if(obtainViewModel().state){
            //-多图片预览
            ///选择模式-点击未选图片
            if(!pic.isSelected()){
                picUrls.add(CommonUtil.parseFaceImageUrl(pic.getImgUrl()));
                Intent intent = new Intent(this, PictureViewerActivity.class);
                intent.putStringArrayListExtra("urls", picUrls);
                intent.putExtra("delete",true);
                intent.putExtra("online",true);
                List<FacePicture> list = new ArrayList<>();
                list.add(pic);
                intent.putExtra("onlineList",new Gson().toJson(list));
                startActivity(intent);
                return;
            }
            ///选择模式-点击已选图片
            Intent intent = new Intent(this, PictureViewerActivity.class);
            List<FacePicture> list = new ArrayList<>();
            for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
                FacePicture picture = obtainViewModel().pictureList.get(i);
                if(picture.isSelected()){
                    picUrls.add(CommonUtil.parseFaceImageUrl(picture.getImgUrl()));
                    list.add(picture);
                }
            }
            //已选0张图片
            if(picUrls.isEmpty()){
                picUrls.add(CommonUtil.parseFaceImageUrl(pic.getImgUrl()));
            }

            int index = 0;
            for (int m = 0; m < picUrls.size(); m++) {
                String url = picUrls.get(m);
                if(Objects.equals(url, CommonUtil.parseFaceImageUrl(pic.getImgUrl()))){
                    index = m;
                    break;
                }
            }
            intent.putStringArrayListExtra("urls", picUrls);
            intent.putExtra("index",index);
            intent.putExtra("delete",true);
            intent.putExtra("online",true);
            intent.putExtra("onlineList",new Gson().toJson(list));
            startActivity(intent);
        }else{
            //-单图片预览
            Intent intent = new Intent(this, PictureViewerActivity.class);
            picUrls.add(CommonUtil.parseFaceImageUrl(pic.getImgUrl()));
            intent.putStringArrayListExtra("urls", picUrls);
            intent.putExtra("delete",true);
            intent.putExtra("online",true);
            List<FacePicture> list = new ArrayList<>();
            list.add(pic);
            intent.putExtra("onlineList",new Gson().toJson(list));
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(FacePicture pic) {
        for (int i = 0; i < obtainViewModel().pictureList.size(); i++) {
            FacePicture picture = obtainViewModel().pictureList.get(i);
            if(Objects.equals(picture.getId(), pic.getId())){
                picture.setSelected(!picture.isSelected());
                obtainViewModel().updateDataSelected(i);
                return;
            }
        }
    }
}