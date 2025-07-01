package com.ehaohai.robot.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.AMapException;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class CustomNaviActivity extends BaseActivity implements AMapNaviViewListener {

    private AMapNaviView mAMapNaviView;
    private AMapNavi mAMapNavi;

    private NaviLatLng startLatLng;
    private NaviLatLng endLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullScreen(this);
        setContentView(R.layout.activity_custom_navi);

        // 接收起点终点经纬度与名称
        double startLat = getIntent().getDoubleExtra("start_lat", 0);
        double startLng = getIntent().getDoubleExtra("start_lng", 0);
        double endLat = getIntent().getDoubleExtra("end_lat", 0);
        double endLng = getIntent().getDoubleExtra("end_lng", 0);

        startLatLng = new NaviLatLng(startLat, startLng);
        endLatLng = new NaviLatLng(endLat, endLng);

        // 初始化导航视图
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);

        // 自定义导航视图配置（可按需调节）
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0); // 0°俯视
        mAMapNaviView.setViewOptions(options);

        try {
            mAMapNavi = AMapNavi.getInstance(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNavi.addAMapNaviListener(new AMapNaviListenerAdapter());

        List<NaviLatLng> startList = new ArrayList<>();
        List<NaviLatLng> endList = new ArrayList<>();
        startList.add(startLatLng);
        endList.add(endLatLng);

        try {
            int strategy = mAMapNavi.strategyConvert(
                    false, // 不避让拥堵
                    false, // 不避让高速
                    false, // 不避让收费
                    false, // 不高速优先
                    false  // 不躲避限行
            );
            mAMapNavi.calculateDriveRoute(startList, endList, null, strategy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    private class AMapNaviListenerAdapter implements AMapNaviListener {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onInitNaviSuccess() {}

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onTrafficStatusUpdate() {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onGetNavigationText(int i, String s) {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onEndEmulatorNavi() {

        }

        @Override
        public void onArriveDestination() {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {
            mAMapNavi.startNavi(NaviType.GPS);
        }

        @Override
        public void notifyParallelRoad(int i) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

        }

        @Override
        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

        }

        @Override
        public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

        }

        @Override
        public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

        }

        @Override
        public void onPlayRing(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

        }

        @Override
        public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

        }

        @Override
        public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

        }

        @Override
        public void onGpsSignalWeak(boolean b) {

        }

        @Override
        public void onCalculateRouteFailure(int errorCode) {
            Log.e("CustomNaviActivity", "路径规划失败，错误码：" + errorCode);
        }

        @Override
        public void onReCalculateRouteForYaw() {

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onGpsOpenStatus(boolean b) {

        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {

        }

        @Override
        public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

        }

        @Override
        public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

        }

        @Override
        public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

        }

        @Override
        public void showCross(AMapNaviCross aMapNaviCross) {

        }

        @Override
        public void hideCross() {

        }

        @Override
        public void showModeCross(AMapModelCross aMapModelCross) {

        }

        @Override
        public void hideModeCross() {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

        }

        @Override
        public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

        }

        @Override
        public void hideLaneInfo() {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
        // 暂停语音播报
        //mAMapNavi.stopSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
    }
}
