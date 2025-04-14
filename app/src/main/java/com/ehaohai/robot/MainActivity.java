package com.ehaohai.robot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityMainBinding;
import com.ehaohai.robot.event.Exit;
import com.ehaohai.robot.ui.activity.ControlActivity;
import com.ehaohai.robot.ui.activity.DeviceListActivity;
import com.ehaohai.robot.ui.activity.MineActivity;
import com.ehaohai.robot.ui.activity.ModeActivity;
import com.ehaohai.robot.ui.activity.PictureListActivity;
import com.ehaohai.robot.ui.activity.WarnListActivity;
import com.ehaohai.robot.ui.viewmodel.MainViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private void init_() {

    }

    private void bind_() {
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
        binding.mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MineActivity.class));
            }
        });
        binding.device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
            }
        });
        binding.modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ModeActivity.class));
            }
        });
        binding.warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WarnListActivity.class));
            }
        });
        binding.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PictureListActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.modeText.setText(CommonData.mode);
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
        if(CommonData.networkMode){
            exit();
        }else{
            finish();
        }
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
            finish();
        }
    }
}