package com.ehaohai.robot.utils;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;

public class NetworkSpeedMonitor {
    private long lastRxBytes = 0;
    private long lastTxBytes = 0;
    private long lastTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int interval = 1000; // 1秒刷新一次

    public interface SpeedListener {
        void onSpeedUpdate(long downloadSpeed, long uploadSpeed);
    }

    private SpeedListener speedListener;

    public NetworkSpeedMonitor(SpeedListener listener) {
        this.speedListener = listener;
    }

    private final Runnable speedRunnable = new Runnable() {
        @Override
        public void run() {
            long currentRxBytes = TrafficStats.getTotalRxBytes(); // 设备总下载流量
            long currentTxBytes = TrafficStats.getTotalTxBytes(); // 设备总上传流量
            long currentTime = System.currentTimeMillis();

            if (lastRxBytes > 0 && lastTxBytes > 0 && lastTime > 0) {
                long downloadSpeed = (currentRxBytes - lastRxBytes) * 1000 / (currentTime - lastTime); // B/s
                long uploadSpeed = (currentTxBytes - lastTxBytes) * 1000 / (currentTime - lastTime);

                if (speedListener != null) {
                    speedListener.onSpeedUpdate(downloadSpeed, uploadSpeed);
                }
            }

            lastRxBytes = currentRxBytes;
            lastTxBytes = currentTxBytes;
            lastTime = currentTime;

            handler.postDelayed(this, interval);
        }
    };

    public void startMonitoring() {
        lastRxBytes = TrafficStats.getTotalRxBytes();
        lastTxBytes = TrafficStats.getTotalTxBytes();
        lastTime = System.currentTimeMillis();
        handler.post(speedRunnable);
    }

    public void stopMonitoring() {
        handler.removeCallbacks(speedRunnable);
    }
}