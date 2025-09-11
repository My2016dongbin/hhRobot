package com.ehaohai.robot.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.idst.nui.Constants;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.NaviSetting;
import com.bumptech.glide.Glide;
import com.ehaohai.robot.MainActivity;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityMapLocationBinding;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.viewmodel.MapLocationViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;

import java.util.ArrayList;
import java.util.List;

public class MapLocationActivity extends BaseLiveActivity<ActivityMapLocationBinding, MapLocationViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        NaviSetting.updatePrivacyShow(MapLocationActivity.this, true, true);
        NaviSetting.updatePrivacyAgree(MapLocationActivity.this, true);
        binding.mapView.onCreate(savedInstanceState);
        obtainViewModel().aMap = binding.mapView.getMap();
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        ///打点
        if(CommonData.warnList.size()>0){
            for (int i = 0; i < CommonData.warnList.size(); i++) {
                try {
                    Warn warn = CommonData.warnList.get(i);
                    Warn.LocationData locationData = warn.getLocationData();
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(new LatLng(locationData.getLatitude(),locationData.getLongitude()));
                    markerOption.title(warn.getDeviceName());

                    markerOption.draggable(false);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.icon_marker)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    Marker marker = obtainViewModel().aMap.addMarker(markerOption);
                    Bundle bundle = new Bundle();
                    bundle.putString("id",warn.getId());
                    bundle.putString("time",warn.getTimeStamp());
                    if(warn.getMoreInfo()!=null){
                        bundle.putString("name",warn.getMoreInfo().getAlarmInfo());
                        bundle.putString("type",warn.getMoreInfo().getName());
                    }
                    bundle.putString("url",warn.getImgPath());
                    marker.setObject(bundle);
                }catch (Exception e){
                    //
                }
            }
        }
        try{
            ///地图移动到第一条
            Warn.LocationData locationData = CommonData.warnList.get(0).getLocationData();
            obtainViewModel().aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationData.getLatitude(),locationData.getLongitude()),16));
        }catch (Exception e){
            //
        }
        /*if(CommonData.bugList.size()>0){
            for (int i = 0; i < CommonData.bugList.size(); i++) {
                try {
                    Warn warn = CommonData.bugList.get(i);
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(new LatLng(36.289519,120.327248));
                    markerOption.title(warn.getDeviceName());

                    markerOption.draggable(false);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.icon_marker)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    obtainViewModel().aMap.addMarker(markerOption);
                }catch (Exception e){
                    //
                }
            }
        }*/

        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = (Bundle) marker.getObject();
                binding.warnName.setText(bundle.getString("name")+"");
                binding.warnTime.setText(bundle.getString("time")+"");
                binding.warnType.setText(bundle.getString("type")+"");
                Glide.with(MapLocationActivity.this).load(bundle.getString("url")+"")
                                .error(getResources().getDrawable(R.drawable.error_pic))
                        .into(binding.warnImage);
                binding.warnLayout.openDrawer(GravityCompat.END);
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        obtainViewModel().aMap.setOnMarkerClickListener(markerClickListener);

    }

    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.guide, new Action() {
            @Override
            public void click() {
                //起点
                Poi start = new Poi("北京首都机场", new LatLng(40.080525,116.603039), "B000A28DAE");
                //途经点
                List<Poi> poiList = new ArrayList();
                poiList.add(new Poi("故宫", new LatLng(39.918058,116.397026), "B000A8UIN8"));
                //终点
                Poi end = new Poi("北京大学", new LatLng(39.941823,116.426319), "B000A816R6");
                // 组件参数配置
                AmapNaviParams params = new AmapNaviParams(start, poiList, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                // 启动组件
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


                NaviSetting.updatePrivacyShow(MapLocationActivity.this, true, true);
                NaviSetting.updatePrivacyAgree(MapLocationActivity.this, true);
                Intent intent = new Intent(MapLocationActivity.this, CustomNaviActivity.class);
                intent.putExtra("start_lat", CommonData.lat);
                intent.putExtra("start_lng", CommonData.lng);
                intent.putExtra("end_lat", 39.917337);
                intent.putExtra("end_lng", 116.397056);
                startActivity(intent);
            }
        });
    }

    @Override
    protected ActivityMapLocationBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_map_location);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MapLocationViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MapLocationViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();

        CommonData.warnList = new ArrayList<>();
        CommonData.bugList = new ArrayList<>();
    }
}