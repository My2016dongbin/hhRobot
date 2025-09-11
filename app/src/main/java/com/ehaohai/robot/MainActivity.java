package com.ehaohai.robot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.NaviSetting;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityMainBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.event.UDPMessage;
import com.ehaohai.robot.model.Heart;
import com.ehaohai.robot.model.UdpMessage;
import com.ehaohai.robot.ui.activity.ControlActivity;
import com.ehaohai.robot.ui.activity.CustomNaviActivity;
import com.ehaohai.robot.ui.activity.DeviceListActivity;
import com.ehaohai.robot.ui.activity.MineActivity;
import com.ehaohai.robot.ui.activity.ModeActivity;
import com.ehaohai.robot.ui.activity.PictureListActivity;
import com.ehaohai.robot.ui.activity.TaskListActivity;
import com.ehaohai.robot.ui.activity.WarnListActivity;
import com.ehaohai.robot.ui.service.PersistentForegroundService;
import com.ehaohai.robot.ui.viewmodel.MainViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseLiveActivity<ActivityMainBinding, MainViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        EventBus.getDefault().register(this);
        init_();
        bind_();
    }

    ///退出登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Exit event) {
        finish();
    }


    ///设备心跳数据
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Heart heart) {
        binding.battery.setPower(heart.getBatteryPercentage());
        binding.batteryText.setText(heart.getBatteryPercentage()+"");
    }


    ///UDP消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(UDPMessage event) {
        String message = event.getMessage();
        try {
            JSONObject object = new JSONObject(message);
            String msgType = object.getString("msgType");
            String deviceSn = object.getString("DeviceSn");
            String timestamp = object.getString("timestamp");
            String IP = object.getString("IP");

            List<String> folderNames = CommonUtil.getRobotFileList();
            HhLog.e("folderNames Config " + folderNames);

            for (int i = 0; i < folderNames.size(); i++) {
                String udpDeviceSn = folderNames.get(i);
                if(Objects.equals(udpDeviceSn, deviceSn)){
                    CommonUtil.refreshRobotFileIP(deviceSn,IP);
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void init_() {
        ///启动心跳服务
        Intent intent = new Intent(this, PersistentForegroundService.class);
        startService(intent);
    }

    private void bind_() {
        CommonUtil.click(binding.guide, new Action() {
            @Override
            public void click() {
                //起点
                /*Poi start = new Poi("北京首都机场", new LatLng(40.080525,116.603039), "B000A28DAE");
                //途经点
                List<Poi> poiList = new ArrayList();
                poiList.add(new Poi("故宫", new LatLng(39.918058,116.397026), "B000A8UIN8"));
                //终点
                Poi end = new Poi("北京大学", new LatLng(39.941823,116.426319), "B000A816R6");
                // 组件参数配置
                AmapNaviParams params = new AmapNaviParams(start, poiList, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                // 启动组件
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);*/


                /*NaviSetting.updatePrivacyShow(MainActivity.this, true, true);
                NaviSetting.updatePrivacyAgree(MainActivity.this, true);
                Intent intent = new Intent(MainActivity.this, CustomNaviActivity.class);
                intent.putExtra("start_lat", CommonData.lat);
                intent.putExtra("start_lng", CommonData.lng);
                intent.putExtra("end_lat", 39.917337);
                intent.putExtra("end_lng", 116.397056);
                startActivity(intent);*/

                startActivity(new Intent(MainActivity.this, TaskListActivity.class));

            }
        });
        CommonUtil.click(binding.llDeviceChoose, new Action() {
            @Override
            public void click() {

            }
        });
        CommonUtil.click(binding.enter, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(MainActivity.this, ControlActivity.class));
            }
        });
        CommonUtil.click(binding.mine, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(MainActivity.this, MineActivity.class));
            }
        });
        CommonUtil.click(binding.device, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
            }
        });
        CommonUtil.click(binding.modeButton, new Action() {
            @Override
            public void click() {
                Toast.makeText(MainActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(MainActivity.this, ModeActivity.class));
            }
        });
        CommonUtil.click(binding.warn, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(MainActivity.this, WarnListActivity.class));
            }
        });
        CommonUtil.click(binding.picture, new Action() {
            @Override
            public void click() {
                startActivity(new Intent(MainActivity.this, PictureListActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.modeText.setText(CommonData.mode);
        if(!PersistentForegroundService.isRunning){
            init_();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected ActivityMainBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MainViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MainViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private boolean isExit = false;
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次回到主页", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        } else {
            finishAffinity();
        }
    }
}