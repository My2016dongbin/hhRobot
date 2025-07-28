package com.ehaohai.robot.ui.activity;
import android.content.Intent;;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.constant.URLConstant;
import com.ehaohai.robot.databinding.ActivityDeviceListBinding;
import com.ehaohai.robot.event.DeviceRefresh;
import com.ehaohai.robot.ui.viewmodel.DeviceListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.SPUtils;
import com.ehaohai.robot.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class DeviceListActivity extends BaseLiveActivity<ActivityDeviceListBinding, DeviceListViewModel> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///设备刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(DeviceRefresh event) {
        obtainViewModel().postDeviceList();
    }

    private void init_() {
        obtainViewModel().postDeviceList();
    }

    private void bind_() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected ActivityDeviceListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_device_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public DeviceListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(DeviceListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().device.observe(this, this::nameChanged);
    }

    private void nameChanged(String changed) {
        binding.messageList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < obtainViewModel().deviceList.size();i++){
            String finalSn = obtainViewModel().deviceList.get(i).getSn();
            View view = inflater.inflate(R.layout.item_device,null,false);
            TextView from = view.findViewById(R.id.from);
            from.setText("添加设备");
            TextView name = view.findViewById(R.id.name);
            name.setText(obtainViewModel().deviceList.get(i).getName());
            TextView code = view.findViewById(R.id.code);
            code.setText(finalSn);
            TextView state = view.findViewById(R.id.state);
            if(Objects.equals(finalSn, CommonData.sn)){
                state.setText("已连接");
            }else{
                state.setText("未连接");
            }
            View viewEmpty = view.findViewById(R.id.view);
            if(i == 0){
                viewEmpty.setVisibility(View.VISIBLE);
            }else{
                viewEmpty.setVisibility(View.GONE);
            }
            CommonUtil.click(view, new Action() {
                @Override
                public void click() {
                    //startActivity(new Intent(DeviceListActivity.this, DeviceSettingActivity.class));
                    String robotToken = CommonUtil.getRobotFileToken(finalSn);
                    String robotIp = CommonUtil.getRobotFileIP(finalSn);
                    URLConstant.setLocalPath(robotIp);
                    if(robotToken.isEmpty()){
                        Intent intent = new Intent(DeviceListActivity.this, OfflineLoginActivity.class);
                        intent.putExtra("sn",finalSn);
                        startActivity(intent);
                        ///TODO 此处可能返回没有登录所选新设备，返回后应通过CommonData.sn查询设备信息重新设置ip（URLConstant.setLocalPath(robotIp);）
                    }else{
                        startActivity(new Intent(DeviceListActivity.this, MainActivity.class));
                        CommonData.token = robotToken;
                        SPUtils.put(HhApplication.getInstance(), SPValue.token, CommonData.token);
                        CommonData.sn = finalSn;
                        SPUtils.put(HhApplication.getInstance(), SPValue.sn, finalSn);
                        SPUtils.put(HhApplication.getInstance(), SPValue.login, true);
                        SPUtils.put(DeviceListActivity.this, SPValue.offlineIp,robotIp);
                    }
                }
            });
            binding.messageList.addView(view);
        }
        if(obtainViewModel().deviceList.isEmpty()){
            ///空列表-dog
            View dog = inflater.inflate(R.layout.item_dog_add,null,false);
            LinearLayout ll_dog = dog.findViewById(R.id.ll_add);
            CommonUtil.click(ll_dog, new Action() {
                @Override
                public void click() {
                    startActivity(new Intent(DeviceListActivity.this,DeviceSearchActivity.class));
                }
            });
            binding.messageList.addView(dog);
        }else{
            View add = inflater.inflate(R.layout.item_device_add,null,false);
            CommonUtil.click(add, new Action() {
                @Override
                public void click() {
                    startActivity(new Intent(DeviceListActivity.this,DeviceSearchActivity.class));
                }
            });
            binding.messageList.addView(add);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}