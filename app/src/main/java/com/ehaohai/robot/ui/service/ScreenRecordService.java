// ✅ ScreenRecorderManager.java
// 用于启动系统录屏并保存至系统相册，兼容 Android 7 ~ 14

package com.ehaohai.robot.ui.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ehaohai.robot.utils.CommonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ScreenRecordService extends Service {

    public static final String RESULT_CODE = "result_code";
    public static final String RESULT_DATA = "result_data";
    public static final String CHANNEL_ID = "record_channel";
    public static final String ACTION_STOP_RECORDING = "action_stop_recording";

    private static ScreenRecordService instance;

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private String videoPath;

    public static void stopRecording(Context context) {
        Intent stopIntent = new Intent(context, ScreenRecordService.class);
        stopIntent.setAction(ACTION_STOP_RECORDING);
        ContextCompat.startForegroundService(context, stopIntent);
        Toast.makeText(context, "视频已保存", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;

        if (intent != null && ACTION_STOP_RECORDING.equals(intent.getAction())) {
            stopAndSave();
            return START_NOT_STICKY;
        }

        try {
            startForeground(1, createNotification());

            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(RESULT_DATA);

            MediaProjectionManager mgr = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            }
            if (mgr != null && data != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaProjection = mgr.getMediaProjection(resultCode, data);
                }
                startRecording();
            } else {
                Log.e("ScreenRecordService", "MediaProjectionManager or Intent data is null");
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void startRecording() {
        try {
            int width = 1080;
            int height = 1920;
            int dpi = getResources().getDisplayMetrics().densityDpi;

            mediaRecorder = new MediaRecorder();
            videoPath = getExternalCacheDir() + "/record_" + /*UUID.randomUUID()*/System.currentTimeMillis() + ".mp4";

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(videoPath);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setVideoEncodingBitRate(8 * 1024 * 1024);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(width, height);

            mediaRecorder.prepare();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                        width, height, dpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mediaRecorder.getSurface(), null, null);
            }

            mediaRecorder.start();

            // 示例：10 秒后自动停止
            // new Handler().postDelayed(this::stopAndSave, 10000);
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    public void stopAndSave() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
            if (mediaProjection != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaProjection.stop();
                }
                mediaProjection = null;
            }
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }

            CommonUtil.saveVideoToFilePicture(this, new File(videoPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopSelf();
        }
    }

    private Notification createNotification() {
        try {
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel(CHANNEL_ID, "录屏", NotificationManager.IMPORTANCE_LOW);
            }
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("连接成功")
                .setContentText("已成功连接机器狗")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
