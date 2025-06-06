package com.ehaohai.robot.utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.icu.text.DateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ehaohai.robot.HhApplication;
import com.ehaohai.robot.R;
import com.ehaohai.robot.ui.activity.AudioListActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class CommonUtil {

    /**
     * 权限判断
     * @param context 上下文
     * @param permissionCode 权限编码
     * @return
     */
    public static boolean hasPermission(Context context , String permissionCode){
        String permissions = (String) SPUtils.get(context,SPValue.permission,"");
        //HhLog.e("hasPermission: " + permissions );
        if(permissions == null){
            return false;
        }

        return permissions.contains(permissionCode+"_");
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void captureSurfaceView(SurfaceView surfaceView, Activity activity) {
        Bitmap bitmap = Bitmap.createBitmap(
                surfaceView.getWidth(),
                surfaceView.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Surface surface = surfaceView.getHolder().getSurface();
        if (!surface.isValid()) {
//            Toast.makeText(activity, "Surface 无效，无法截图", Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, "截图失败: 未播放视频", Toast.LENGTH_SHORT).show();
            return;
        }

        PixelCopy.request(surface, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                saveBitmapToGallery(activity, bitmap);
            } else {
//                Toast.makeText(activity, "截图失败: " + copyResult, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "截图失败: 未播放视频", Toast.LENGTH_SHORT).show();
            }
        }, new Handler(Looper.getMainLooper()));
    }
    public static long screenShootTimes = 0;
    public static void saveBitmapToGallery(Context context, Bitmap bitmap) {
        String filename = "screenshot_" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/iot");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream os = resolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            long now = new Date().getTime();
            if(now - screenShootTimes < 2000){
                return;
            }
            screenShootTimes = now;
            Toast.makeText(context, "截图已保存", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




        public static void downloadImageToGallery(Context context, String imageUrl) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToAlbum(context, resource, "downloaded_image_" + System.currentTimeMillis() + ".jpg");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    public static void saveImageToAlbum(Context context, Bitmap bitmap, String fileName) {
        OutputStream fos;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/iot"); // 自定义目录
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri = resolver.insert(collection, values);

            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                if (fos != null) fos.close();

                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(imageUri, values, null, null);

                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static int parseInt(String content){
        int count = 100;
        try{
            return Integer.parseInt(content);
        }catch (Exception e){
            if(content!=null) {
                content = content.substring(0,content.indexOf("."));
                return Integer.parseInt(content);
            }else{
                return count;
            }
        }
    }
    public static String parseContent(String content){
        if(content!=null) {
            StringBuilder buffer = new StringBuilder();
            String[] split = content.split("\\\\n");
            for (int i = 0; i < split.length; i++) {
                buffer.append(split[i]).append("\n");
            }
            return buffer.toString();
        }else{
            return "";
        }
    }
    public static String parseZero(int num){
        if(num > 9){
            return num +"";
        }else {
            return "0" + num;
        }
    }
    public static String parseLongTime(long times){
        String t = times+"";
        Date date = new Date(times);
        try{
            t = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
        }catch (Exception e){
            //
        }
        return t;
    }

    public static void enterWhiteListSetting(Context context){
        try {
            context.startActivity(getAutostartSettingIntent(context));
        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
    /**
     * 获取自启动管理页面的Intent
     * @param context context
     * @return 返回自启动管理页面的Intent
     * */
    public static Intent getAutostartSettingIntent(Context context) {
        ComponentName componentName = null;

        String brand = Build.MANUFACTURER;

        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (brand.toLowerCase()) {
            case "samsung"://三星

                componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");

                break;

            case "huawei"://华为

//荣耀V8，EMUI 8.0.0，Android 8.0上，以下两者效果一样

                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");

// componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");//目前看是通用的

                break;

            case "xiaomi"://小米

                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");

                break;

            case "vivo"://VIVO

// componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safaguard.PurviewTabActivity");

                componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");

                break;

            case "oppo"://OPPO

// componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");

                componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");

                break;

            case "yulong":

            case "360"://360

                componentName = new ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");

                break;

            case "meizu"://魅族

                componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");

                break;

            case "oneplus"://一加

                componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");

                break;

            case "letv"://乐视

                intent.setAction("com.letv.android.permissionautoboot");

            default://其他

                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");

                intent.setData(Uri.fromParts("package", context.getPackageName(), null));

                break;

        }

        intent.setComponent(componentName);

        return intent;

    }

    /**
     * 地球半径，单位：公里/千米
     */
    private static final double EARTH_RADIUS = 6378.137;


    /**
     * 返回两个地理坐标之间的距离
     * @param firsLongitude 第一个坐标的经度
     * @param firstLatitude 第一个坐标的纬度
     * @param secondLongitude 第二个坐标的经度
     * @param secondLatitude  第二个坐标的纬度
     * @return 两个坐标之间的距离，单位：公里/千米
     */
    public static double distance(double firsLongitude, double firstLatitude,
                                  double secondLongitude, double secondLatitude) {
        double firstRadianLongitude = radian(firsLongitude);
        double firstRadianLatitude = radian(firstLatitude);
        double secondRadianLongitude = radian(secondLongitude);
        double secondRadianLatitude = radian(secondLatitude);

        double a = firstRadianLatitude - secondRadianLatitude;
        double b = firstRadianLongitude - secondRadianLongitude;
        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(firstRadianLatitude) * Math.cos(secondRadianLatitude)
                * Math.pow(Math.sin(b / 2), 2)));
        cal = cal * EARTH_RADIUS;
        double ret = Math.round(cal * 10000d) / 10000d;
        Log.e("CommonUtil", "distance: " + ret );
        return ret;
    }

    /**
     * 经纬度转化成弧度
     * @param d  经度/纬度
     * @return  经纬度转化成的弧度
     */
    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * 保留小数点后几位
     * @param str 被转换者
     * @param count 保留的位数
     * @return
     */
    public static String parsePointCount(String str, int count) {
        String result = str;
        int index = str.indexOf(".");
        if(index > 0 && str.length() > (index+count+1)){
            result = str.substring(0,index+count+1);
        }
        return result;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    /**
     * Get activity from context object
     *
     * @param context something
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof TintContextWrapper) {
            return scanForActivity(((TintContextWrapper) context).getBaseContext());
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取ActionBar高度
     *
     * @param activity activity
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }


    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = CommonUtil.getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.hide();
                }
            }
        }
        if (statusBar) {
            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                CommonUtil.getAppCompActivity(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = CommonUtil.getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.show();
                }
            }
        }

        if (statusBar) {
            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                CommonUtil.getAppCompActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    public static void hideNavKey(Context context) {
        if (Build.VERSION.SDK_INT >= 29) {
            //       设置屏幕始终在前面，不然点击鼠标，重新出现虚拟按键
            ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                            // bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //       设置屏幕始终在前面，不然点击鼠标，重新出现虚拟按键
            ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                            // bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
            );
        }
    }

    public static void showNavKey(Context context, int systemUiVisibility) {
        ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }


    /**
     * Get AppCompatActivity from context
     *
     * @param context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }


    /**
     * dip转为PX
     */
    public static int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    /**
     * 下载速度文本
     */
    public static String getTextSpeed(long speed) {
        String text = "";
        if (speed >= 0 && speed < 1024) {
            text = speed + " KB/s";
        } else if (speed >= 1024 && speed < (1024 * 1024)) {
            text = speed / 1024 + " KB/s";
        } else if (speed >= (1024 * 1024) && speed < (1024 * 1024 * 1024)) {
            text = speed / (1024 * 1024) + " MB/s";
        }
        return text;
    }

    /**
     * String list split ","
     */
    public static List<String> parseListString(String str) {
        List<String> list = new ArrayList<>();
        if(str != null && !str.isEmpty()){
            list = Arrays.asList(str.split(",").clone()); ;
        }
        return list;
    }
    /**
     * String null ==> "" ==> defStr
     */
    public static String parseNullString(String str,String defStr) {
        String text = str;
        if(str == null || str.isEmpty()){
            text = defStr;
        }
        return text;
    }
    /**
     * String null ==> "" ==> defStr
     */
    public static String parse19String(String str,String defStr) {
        String text = str;
        if(str == null || str.isEmpty()){
            return defStr;
        }
        try{
            text = str.replace("T"," ").substring(0,19);
        }catch(Exception e){
            HhLog.e(e.getMessage());
        }
        return text;
    }

    /**
     * 获取当前日期是星期几
     * @param date 时间
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    /**
     * 获取当前日期是周几
     * @param date 时间
     * @return
     */
    public static String getWeekOfDateShort(Date date) {
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    /**
     * 和风天气图标
     * @param color 颜色#以后部分
     * @param icon 和风天气图标id
     * @return
     */
    public static String getHeFengIcon(String color,String icon,String size) {
        String url = "data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" width=\""+size+"\" height=\""+size+"\" fill='%23"+color+"' class=\"qi-"+icon+"-fill\" viewBox=\"0 0 16 16\">" + getIconPath(icon) + "</svg>";

        return url;
    }

    public static String getIconPath(String id){
        String path = "<path d=\"M8.005 3.5a4.5 4.5 0 1 0 4.5 4.5 4.5 4.5 0 0 0-4.5-4.5zm.001-.997a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 1 1 1 0v1.5a.5.5 0 0 1-.5.5z\"/>\n" +
                "  <path d=\"M8.006 2.503a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 1 1 1 0v1.5a.5.5 0 0 1-.5.5zM3.765 4.255a.498.498 0 0 1-.353-.147L2.35 3.048a.5.5 0 0 1 .707-.707L4.12 3.4a.5.5 0 0 1-.354.854zM2.003 8.493h-1.5a.5.5 0 0 1 0-1h1.5a.5.5 0 0 1 0 1zm.691 5.303a.5.5 0 0 1-.354-.854l1.062-1.06a.5.5 0 0 1 .707.707l-1.062 1.06a.498.498 0 0 1-.353.147zm5.299 2.201a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 0 1 1 0v1.5a.5.5 0 0 1-.5.5zm5.302-2.191a.498.498 0 0 1-.353-.147l-1.06-1.06a.5.5 0 1 1 .706-.707l1.06 1.06a.5.5 0 0 1-.353.854zm2.202-5.299h-1.5a.5.5 0 1 1 0-1h1.5a.5.5 0 0 1 0 1zm-3.252-4.242a.5.5 0 0 1-.354-.854l1.06-1.06a.5.5 0 0 1 .708.707l-1.06 1.06a.498.498 0 0 1-.354.147z\"/>\n";
        switch (id){
            case "100":
                path = "<path d=\"M8.005 3.5a4.5 4.5 0 1 0 4.5 4.5 4.5 4.5 0 0 0-4.5-4.5zm.001-.997a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 1 1 1 0v1.5a.5.5 0 0 1-.5.5z\"/>\n" +
                        "  <path d=\"M8.006 2.503a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 1 1 1 0v1.5a.5.5 0 0 1-.5.5zM3.765 4.255a.498.498 0 0 1-.353-.147L2.35 3.048a.5.5 0 0 1 .707-.707L4.12 3.4a.5.5 0 0 1-.354.854zM2.003 8.493h-1.5a.5.5 0 0 1 0-1h1.5a.5.5 0 0 1 0 1zm.691 5.303a.5.5 0 0 1-.354-.854l1.062-1.06a.5.5 0 0 1 .707.707l-1.062 1.06a.498.498 0 0 1-.353.147zm5.299 2.201a.5.5 0 0 1-.5-.5v-1.5a.5.5 0 0 1 1 0v1.5a.5.5 0 0 1-.5.5zm5.302-2.191a.498.498 0 0 1-.353-.147l-1.06-1.06a.5.5 0 1 1 .706-.707l1.06 1.06a.5.5 0 0 1-.353.854zm2.202-5.299h-1.5a.5.5 0 1 1 0-1h1.5a.5.5 0 0 1 0 1zm-3.252-4.242a.5.5 0 0 1-.354-.854l1.06-1.06a.5.5 0 0 1 .708.707l-1.06 1.06a.498.498 0 0 1-.354.147z\"/>\n";
                break;
            case "101":
                path =  "<path d=\"M4.745 1.777a.516.516 0 0 0 .503.404.535.535 0 0 0 .112-.012.517.517 0 0 0 .392-.616L5.496.403A.516.516 0 0 0 4.49.627zM1.023 3.535l.994.633a.516.516 0 0 0 .554-.87l-.994-.633a.516.516 0 0 0-.554.87zM.628 8.043l1.15-.256a.516.516 0 1 0-.223-1.008l-1.15.256a.516.516 0 0 0 .111 1.02.535.535 0 0 0 .112-.012zm10.238-2.28a.535.535 0 0 0 .112-.012l1.15-.256a.516.516 0 1 0-.224-1.008l-1.15.256a.516.516 0 0 0 .112 1.02zM8.522 2.728a.516.516 0 0 0 .712-.158l.633-.994a.516.516 0 0 0-.87-.554l-.633.994a.516.516 0 0 0 .158.712zM2.819 7.032a3.506 3.506 0 0 0 .331.87 3.13 3.13 0 0 0 .908-.486 2.453 2.453 0 0 1-.232-.608 2.504 2.504 0 0 1 1.9-2.988 2.5 2.5 0 0 1 2.988 1.9l.004.038a5.42 5.42 0 0 1 1.064.25 3.509 3.509 0 0 0-.061-.512 3.535 3.535 0 1 0-6.902 1.536z\"/>\n" +
                        "  <path d=\"M12.464 8.48a3.236 3.236 0 0 0-.409.04 4.824 4.824 0 0 0-8.086 0 3.234 3.234 0 0 0-.41-.04 3.285 3.285 0 1 0 1.284 6.31 4.756 4.756 0 0 0 6.338 0 3.286 3.286 0 1 0 1.283-6.31z\"/>\n";
                break;
            case "102":
                path = "<path d=\"m3.402 11.875-.002.002h-.002l-.661.662-.396.397H2.34l-.003.003a.5.5 0 1 0 .707.707l.4-.4.665-.664a.5.5 0 1 0-.707-.707zm10.48-.666a2.145 2.145 0 0 0-.265.026 3.144 3.144 0 0 0-5.225 0 2.145 2.145 0 0 0-.264-.026 2.09 2.09 0 1 0 0 4.18 2.145 2.145 0 0 0 .829-.166 3.109 3.109 0 0 0 4.096 0 2.146 2.146 0 0 0 .829.166 2.09 2.09 0 1 0 0-4.18zm0 3.18a1.144 1.144 0 0 1-.444-.088 1 1 0 0 0-1.038.165 2.109 2.109 0 0 1-2.791 0 1 1 0 0 0-1.038-.165 1.144 1.144 0 0 1-.443.088 1.09 1.09 0 1 1-.007-2.18c.025 0 .05.005.075.009l.074.01a1.023 1.023 0 0 0 .122.007 1 1 0 0 0 .832-.446 2.144 2.144 0 0 1 3.56 0 1 1 0 0 0 .833.446 1.024 1.024 0 0 0 .122-.008l.074-.01c.025-.003.05-.007.068-.008a1.09 1.09 0 1 1 0 2.18zM8.003 2.5a.5.5 0 0 0 .5-.5V.5a.5.5 0 0 0-1 0V2a.5.5 0 0 0 .5.5zM3.408 4.105a.5.5 0 0 0 .707-.707l-1.06-1.06a.5.5 0 0 0-.708.706zM2.5 7.99a.5.5 0 0 0-.5-.5H.5a.5.5 0 0 0 0 1H2a.5.5 0 0 0 .5-.5zm10.993.013a.5.5 0 0 0 .5.5h1.5a.5.5 0 0 0 0-1h-1.5a.5.5 0 0 0-.5.5zm-1.252-3.742a.498.498 0 0 0 .354-.146l1.06-1.061a.5.5 0 0 0-.706-.707l-1.061 1.06a.5.5 0 0 0 .353.854z\"/>\n" +
                        "  <path d=\"M8.014 10.41a3.846 3.846 0 0 1 4.28-1.132 4.452 4.452 0 0 0 .207-1.282 4.5 4.5 0 1 0-6.868 3.812 2.923 2.923 0 0 1 2.381-1.397z\"/>\n";
                break;
            case "103":
                path = "<path d=\"M4.79 10.68a3.56 3.56 0 0 1 1.91-.55h.22a4.56 4.56 0 0 1 3.13-1.22 4.91 4.91 0 0 1 2 .46 4.29 4.29 0 0 0 .33-1.64 4.38 4.38 0 0 0-8.76 0 4.33 4.33 0 0 0 1.17 2.95zM8 2.52a.5.5 0 0 0 .5-.5V.52A.5.5 0 0 0 8 0a.5.5 0 0 0-.5.5V2a.5.5 0 0 0 .5.52zm-4.58 1.6a.48.48 0 0 0 .7 0 .48.48 0 0 0 0-.7L3.06 2.36a.49.49 0 0 0-.7.7zM2.51 8a.5.5 0 0 0-.5-.5H.51a.5.5 0 1 0 0 1H2a.5.5 0 0 0 .51-.5zm10.95 0a.5.5 0 0 0 .5.5h1.5A.5.5 0 0 0 16 8a.5.5 0 0 0-.5-.5H14a.5.5 0 0 0-.54.5zm-1.21-3.72a.47.47 0 0 0 .35-.15l1.06-1.06a.49.49 0 1 0-.7-.7L11.9 3.43a.48.48 0 0 0 0 .7.47.47 0 0 0 .35.15z\"/>\n" +
                        "  <path d=\"M13.58 10.72a2.71 2.71 0 0 0-.59.08 4.12 4.12 0 0 0-2.87-1.23 4.06 4.06 0 0 0-3 1.36 2.78 2.78 0 0 0-.58-.06 2.94 2.94 0 0 0-2.43 1.23 1.62 1.62 0 0 0-.44-.1 1.68 1.68 0 1 0 .89 3.1 3 3 0 0 0 2 .71 3.14 3.14 0 0 0 1.51-.39 4.06 4.06 0 0 0 4.39-.2 2.3 2.3 0 0 0 1.13.3A2.41 2.41 0 0 0 16 13.14a2.52 2.52 0 0 0-2.42-2.42zm0 3.83a1.37 1.37 0 0 1-.66-.18 1 1 0 0 0-.47-.12 1 1 0 0 0-.59.19 2.91 2.91 0 0 1-1.74.56 3 3 0 0 1-1.55-.41 1 1 0 0 0-.51-.14.94.94 0 0 0-.48.13 2.16 2.16 0 0 1-1 .26 2.06 2.06 0 0 1-1.34-.47 1 1 0 0 0-.65-.24c-.19 0-.6.25-.89.25a.68.68 0 0 1 0-1.35c.05 0 .4.06.49.06a.84.84 0 0 0 .78-.43 2 2 0 0 1 1.61-.79c.13 0 .49.07.61.07a1 1 0 0 0 .71-.35 3.13 3.13 0 0 1 2.24-1 3.07 3.07 0 0 1 2.16.93 1 1 0 0 0 .72.3c.1 0 .46-.08.56-.08a1.55 1.55 0 0 1 1.42 1.4 1.42 1.42 0 0 1-1.42 1.41z\"/>\n";
                break;
            case "104":
                path = "<path d=\"M12.603 7.225a3.345 3.345 0 0 0-.423.042 4.987 4.987 0 0 0-8.36 0 3.345 3.345 0 0 0-.423-.042 3.397 3.397 0 1 0 1.326 6.524 4.917 4.917 0 0 0 6.554 0 3.397 3.397 0 1 0 1.326-6.524z\"/>\n" +
                        "  <path d=\"M4.008 6.136a1.545 1.545 0 0 1 1.54-1.467.915.915 0 0 1 .108.012l.084.012a1 1 0 0 0 .961-.445 2.74 2.74 0 0 1 4.598 0 1 1 0 0 0 .961.445l.084-.012a.92.92 0 0 1 .108-.012 1.524 1.524 0 0 1 1.455 2.048 3.379 3.379 0 0 1 .86.538A2.484 2.484 0 0 0 12.136 3.7a3.74 3.74 0 0 0-6.27 0 2.508 2.508 0 0 0-.317-.032A2.548 2.548 0 0 0 3 6.216a2.464 2.464 0 0 0 .069.517 1.705 1.705 0 0 1 .94-.597z\"/>\n";
                break;
            case "300":
                path = "<path d=\"M5.195 1.897h.007a.5.5 0 0 0 .493-.506L5.683.486a.5.5 0 0 0-.5-.493h-.007a.5.5 0 0 0-.493.506l.012.904a.5.5 0 0 0 .5.494zm-2.892.946a.5.5 0 1 0 .698-.716l-.648-.63a.5.5 0 1 0-.697.715zm-.179 2.203a.5.5 0 0 0-.5-.494h-.007l-.904.012a.5.5 0 0 0 .006 1h.007l.905-.012a.5.5 0 0 0 .493-.506zm5.638-2.121a.5.5 0 0 0 .359-.15l.63-.648a.5.5 0 0 0-.716-.698l-.631.647a.5.5 0 0 0 .358.85zm4.254 2.647a2.938 2.938 0 0 0-.37.037 4.364 4.364 0 0 0-7.315 0 2.937 2.937 0 0 0-.37-.037 2.972 2.972 0 1 0 1.16 5.708 4.302 4.302 0 0 0 5.734 0 2.972 2.972 0 1 0 1.161-5.708zM2.47 5.308a3.53 3.53 0 0 1 1.018-.288 1.831 1.831 0 0 1 1.811-1.603 1.809 1.809 0 0 1 .553.094 4.927 4.927 0 0 1 1.282-.404 2.82 2.82 0 0 0-4.67 2.145c0 .02.006.037.006.056zm7.063 9.696a1 1 0 0 0 2 0 6.605 6.605 0 0 0-1-2 6.605 6.605 0 0 0-1 2zm-5.083 0a1 1 0 0 0 2 0 6.605 6.605 0 0 0-1-2 6.605 6.605 0 0 0-1 2z\" style=\"fill-rule:evenodd\"/>\n";
                break;
            case "301":
                path = "<path d=\"M7.012 14.985a1 1 0 0 0 2 0 6.605 6.605 0 0 0-1-2 6.605 6.605 0 0 0-1 2zM3.959 14a1 1 0 0 0 2 0 6.605 6.605 0 0 0-1-2 6.605 6.605 0 0 0-1 2zm6.028 0a1 1 0 0 0 2 0 6.605 6.605 0 0 0-1-2 6.605 6.605 0 0 0-1 2zM5.207 1.904h.007a.5.5 0 0 0 .493-.506L5.695.494a.5.5 0 0 0-.5-.494h-.007a.5.5 0 0 0-.493.506l.012.905a.5.5 0 0 0 .5.493zm-2.892.946a.5.5 0 1 0 .698-.716l-.648-.63a.5.5 0 1 0-.697.715zm-.179 2.203a.5.5 0 0 0-.5-.493h-.007l-.905.011a.5.5 0 0 0 .007 1h.007l.904-.011a.5.5 0 0 0 .494-.507zm5.638-2.12a.5.5 0 0 0 .359-.151l.63-.648a.5.5 0 0 0-.716-.698l-.631.648a.5.5 0 0 0 .358.849z\"/>\n" +
                        "  <path d=\"M12.028 5.579a2.927 2.927 0 0 0-.37.037 4.364 4.364 0 0 0-7.316 0 2.926 2.926 0 0 0-.37-.037 2.972 2.972 0 1 0 1.16 5.709 4.302 4.302 0 0 0 5.735 0 2.972 2.972 0 1 0 1.16-5.71zm-9.546-.264A3.53 3.53 0 0 1 3.5 5.027a1.831 1.831 0 0 1 1.81-1.603 1.81 1.81 0 0 1 .553.095 4.933 4.933 0 0 1 1.281-.405A2.82 2.82 0 0 0 2.476 5.26c0 .02.006.037.006.056z\"/>\n";
                break;
            case "302":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zM2.998 12.5a1 1 0 1 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm8.028 0a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-2.352-.86c-.058 0-.096-.051-.07-.095l.858-1.462c.026-.044-.012-.096-.07-.096h-1.71a.167.167 0 0 0-.145.078l-1.514 2.681c-.045.08.024.172.128.172H7.69c.054 0 .091.045.074.088l-.8 1.976c-.03.07.078.12.134.064l3.227-3.297c.042-.043.006-.109-.06-.109z\"/>\n";
                break;
            case "303":
                path = "<path d=\"M3.685 8.455a3.172 3.172 0 0 0 1.243-.253 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.397-.04 3.184 3.184 0 1 0 0 6.369zM2.998 12.5a1 1 0 1 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-2-1.552a.786.786 0 1 0 1.573 0 5.193 5.193 0 0 0-.787-1.573 5.193 5.193 0 0 0-.786 1.573zm12.429 0a.786.786 0 1 0 1.573 0 5.193 5.193 0 0 0-.786-1.573 5.193 5.193 0 0 0-.787 1.573zM11.026 12.5a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-2.352-.86c-.058 0-.096-.051-.07-.095l.858-1.462c.026-.044-.012-.096-.07-.096h-1.71a.167.167 0 0 0-.145.078l-1.514 2.681c-.045.08.024.172.128.172H7.69c.054 0 .091.045.074.088l-.8 1.976c-.03.07.078.12.134.064l3.227-3.297c.042-.043.006-.109-.06-.109z\"/>\n";
                break;
            case "304":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zM2.998 12.5a1 1 0 1 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm5.676-.86c-.058 0-.096-.051-.07-.095l.858-1.462c.026-.044-.012-.096-.07-.096h-1.71a.167.167 0 0 0-.145.078l-1.514 2.681c-.045.08.024.172.128.172H7.69c.054 0 .091.045.074.088l-.8 1.976c-.03.07.078.12.134.064l3.227-3.297c.042-.043.006-.109-.06-.109z\" style=\"fill-rule:evenodd\"/>\n" +
                        "  <circle cx=\"12.5\" cy=\"12\" r=\"1\"/>\n";
                break;
            case "305":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zM9.6 12.526a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm-5.082 0a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2z\" style=\"fill-rule:evenodd\"/>\n";
                break;
            case "306":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zM7 13.41a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm-4.002-1.717a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm8.003 0a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2z\" style=\"fill-rule:evenodd\"/>\n";
            break;
            case "307":
                path = "<path d=\"M15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zm-8.45 8.179a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm-3.052-1.5a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm6.028 0a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm-8.976-.825a.786.786 0 0 0 1.572 0 5.192 5.192 0 0 0-.786-1.573 5.192 5.192 0 0 0-.786 1.573zm12.43 0a.786.786 0 0 0 1.572 0 5.192 5.192 0 0 0-.787-1.573 5.192 5.192 0 0 0-.786 1.573z\" style=\"fill-rule:evenodd\"/>\n";
            break;
            case "308":
                path = "<path d=\"M6.99 13.449a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm-3.052-1.5a1 1 0 1 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm6.028 0a1 1 0 0 0 2 0 6.606 6.606 0 0 0-1-2 6.606 6.606 0 0 0-1 2zm3.802-1.947a.5.5 0 0 0-.5.5v2.953a.5.5 0 1 0 1 0v-2.953a.5.5 0 0 0-.5-.5zm-11.702 0a.5.5 0 0 0-.5.5v2.953a.5.5 0 1 0 1 0v-2.953a.5.5 0 0 0-.5-.5zm12.02-4.272a1.407 1.407 0 0 0-.172.02.536.536 0 0 1-.061.004.486.486 0 0 1-.407-.22 2.268 2.268 0 0 0-3.803 0 .551.551 0 0 1-.45.213.52.52 0 0 1-.073-.005.793.793 0 0 0-.117-.012 1.39 1.39 0 0 0 0 2.779 1.372 1.372 0 0 0 .542-.11.485.485 0 0 1 .51.082 2.229 2.229 0 0 0 2.978 0 .486.486 0 0 1 .51-.082 1.375 1.375 0 0 0 .543.11 1.39 1.39 0 0 0 0-2.78z\"/>\n" +
                        "  <path d=\"M7.343 8.553c.08 0 .154-.017.233-.022a1.9 1.9 0 0 1 1.48-3.373A2.834 2.834 0 0 1 11.43 3.87a3.295 3.295 0 0 1 .803.116 4.006 4.006 0 0 1 1.836 1.47 2.683 2.683 0 0 0 .115-.648 2.808 2.808 0 0 0-3.267-2.869 4.266 4.266 0 0 0-7.15 0 2.87 2.87 0 0 0-.363-.036 2.905 2.905 0 1 0 1.135 5.58 4.241 4.241 0 0 0 2.803 1.07z\"/>\n";
                break;
            case "309":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-4.211 8.207a.255.255 0 0 0-.325.14l-.966 2.423a.251.251 0 0 0 .14.326.256.256 0 0 0 .093.017.25.25 0 0 0 .232-.157l.966-2.423a.251.251 0 0 0-.14-.326zm-3.728.207a.248.248 0 0 0-.325.137l-.71 1.747a.25.25 0 0 0 .138.325.243.243 0 0 0 .093.019.25.25 0 0 0 .232-.156l.71-1.746a.25.25 0 0 0-.138-.326zm7.247.1a.248.248 0 0 0-.325.136l-.71 1.747a.25.25 0 0 0 .137.326.243.243 0 0 0 .094.018.25.25 0 0 0 .232-.156l.71-1.746a.25.25 0 0 0-.138-.326z\"/>\n";
                break;
            case "310":
                path = "<path d=\"M3.685 8.455a3.172 3.172 0 0 0 1.243-.253 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.397-.04 3.184 3.184 0 1 0 0 6.369zM7 13.713a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-2.527-2.02a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm5.011 0a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-6.732-.833a5.192 5.192 0 0 0-.786-1.573 5.192 5.192 0 0 0-.786 1.573.786.786 0 0 0 1.572 0zm-.347 2.746a.786.786 0 0 0 1.572 0 5.192 5.192 0 0 0-.786-1.573 5.192 5.192 0 0 0-.786 1.573zm10.835-2.738a.786.786 0 0 0 1.573 0 5.194 5.194 0 0 0-.786-1.573 5.194 5.194 0 0 0-.787 1.573zm-1.209 2.738a.786.786 0 0 0 1.573 0 5.192 5.192 0 0 0-.786-1.573 5.192 5.192 0 0 0-.787 1.573z\" style=\"fill-rule:evenodd\"/>\n";
                break;
            case "311":
                path = "<path d=\"M12.254 2.086a3.147 3.147 0 0 0-.397.04 4.675 4.675 0 0 0-7.838 0 3.147 3.147 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.244-6.116zM6.938 13.362a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm-5-1.669a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm10.026 0a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zM.734 8.975a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0V9.475a.5.5 0 0 0-.5-.5zm4.72 1.382a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0v-2a.5.5 0 0 0-.5-.5zm4.996.006a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0v-2a.5.5 0 0 0-.5-.5zm4.816-1.388a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0V9.475a.5.5 0 0 0-.5-.5z\"/>\n";
            break;
            case "312":
                path = "<path d=\"M12.254 2.086a3.147 3.147 0 0 0-.397.04 4.675 4.675 0 0 0-7.838 0 3.147 3.147 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.244-6.116zM.734 8.975a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0V9.475a.5.5 0 0 0-.5-.5zm4.72 1.718a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0v-2a.5.5 0 0 0-.5-.5zM3.06 9.975a.5.5 0 0 0-.5.5v1.998a.5.5 0 0 0 1 0v-1.998a.5.5 0 0 0-.5-.5zm9.903 0a.5.5 0 0 0-.5.5v1.998a.5.5 0 0 0 1 0v-1.998a.5.5 0 0 0-.5-.5zm-5.025 1.382a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0v-2a.5.5 0 0 0-.5-.5zm2.512-.657a.5.5 0 0 0-.5.5v1.998a.5.5 0 1 0 1 0V11.2a.5.5 0 0 0-.5-.5zm4.816-1.725a.5.5 0 0 0-.5.5v1.999a.5.5 0 0 0 1 0V9.475a.5.5 0 0 0-.5-.5z\"/>\n";
                break;
            case "313":
                path = "<path d=\"M15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zM1.148 10.095l.978 1.397.977-1.397-.977-1.396-.978 1.396zm2.938 1.456.977 1.397.978-1.397-.978-1.396-.977 1.396zm2.936 1.789L8 14.736l.977-1.396L8 11.943l-.978 1.397zm2.937-1.789.978 1.397.977-1.397-.977-1.396-.978 1.396zm2.937-1.456.978 1.397.978-1.397-.978-1.396-.978 1.396z\"/>\n";
                break;
            case "314":
                path = "<path d=\"M10.435 12.443a1 1 0 0 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zm1.88-10.357a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-8.917 10.25a.786.786 0 0 0 1.573 0 5.193 5.193 0 0 0-.786-1.572 5.193 5.193 0 0 0-.787 1.572zm5.043-2.186a.502.502 0 0 0-.626.329L6.73 13.958a.5.5 0 0 0 .955.297l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "315":
                path = "<path d=\"M9.435 12.443a1 1 0 0 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zm2.88-10.357a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-9.917 10.25a.786.786 0 0 0 1.573 0 5.193 5.193 0 0 0-.786-1.572 5.193 5.193 0 0 0-.787 1.572zm9.633-1.179a.786.786 0 1 0 1.573 0 5.193 5.193 0 0 0-.786-1.573 5.193 5.193 0 0 0-.787 1.573zm-4.59-1.007a.502.502 0 0 0-.626.329L5.73 13.958a.5.5 0 0 0 .955.297l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "316":
                path = "<path d=\"M10.022 12.443a1 1 0 0 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zm2.746-1.179a1 1 0 1 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zM15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zM3.909 12.336a.786.786 0 0 0 1.573 0 5.193 5.193 0 0 0-.787-1.572 5.193 5.193 0 0 0-.786 1.572zm-2.326-1.179a.786.786 0 1 0 1.572 0 5.193 5.193 0 0 0-.786-1.573 5.193 5.193 0 0 0-.786 1.573zm6.444-1.007a.503.503 0 0 0-.626.329l-1.085 3.479a.5.5 0 0 0 .955.297l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "317":
                path = "<path d=\"M12.768 11.264a1 1 0 1 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zM15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zM1.583 11.157a.786.786 0 1 0 1.572 0 5.193 5.193 0 0 0-.786-1.573 5.193 5.193 0 0 0-.786 1.573zm6.754-1.007a.499.499 0 0 0-.627.329l-1.086 3.479a.5.5 0 0 0 .955.297l1.086-3.478a.5.5 0 0 0-.328-.626zm-2.707.196a.502.502 0 0 0-.626.328l-.818 2.62a.501.501 0 0 0 .328.626.516.516 0 0 0 .15.023.501.501 0 0 0 .477-.351l.818-2.62a.501.501 0 0 0-.328-.626zm5.406 0a.502.502 0 0 0-.627.328l-.818 2.62a.501.501 0 0 0 .329.626.516.516 0 0 0 .149.023.501.501 0 0 0 .478-.351l.817-2.62a.501.501 0 0 0-.328-.626z\"/>\n";
                break;
            case "318":
                path = "<path d=\"M12.768 11.264a1 1 0 1 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zM15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zM1.583 11.157a.786.786 0 1 0 1.572 0 5.193 5.193 0 0 0-.786-1.573 5.193 5.193 0 0 0-.786 1.573zm6.754-1.007a.499.499 0 0 0-.627.329l-1.086 3.479a.5.5 0 0 0 .955.297l1.086-3.478a.5.5 0 0 0-.328-.626zm-2.707.196a.502.502 0 0 0-.626.328l-.818 2.62a.501.501 0 0 0 .328.626.516.516 0 0 0 .15.023.501.501 0 0 0 .477-.351l.818-2.62a.501.501 0 0 0-.328-.626zm5.406 0a.502.502 0 0 0-.627.328l-.818 2.62a.501.501 0 0 0 .329.626.516.516 0 0 0 .149.023.501.501 0 0 0 .478-.351l.817-2.62a.501.501 0 0 0-.328-.626zm-9.312 3.88a.645.645 0 1 0 1.29 0 4.258 4.258 0 0 0-.645-1.29 4.258 4.258 0 0 0-.645 1.29zm11.426.028a.617.617 0 0 0 1.235 0 4.078 4.078 0 0 0-.617-1.235 4.078 4.078 0 0 0-.617 1.235z\" style=\"fill-rule:evenodd\"/>\n";
                break;
            case "399":
                path = "<path d=\"M.28 9.372a4.514 4.514 0 1 0 9.029 0c0-2.495-4.514-9.028-4.514-9.028S.28 6.877.28 9.372zM13.212 6.3s-2.569 3.718-2.569 5.138a2.569 2.569 0 1 0 5.138 0c0-1.42-2.569-5.138-2.569-5.138z\"/>\n";
                break;
            case "400":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-1.055 10.38-.44-.254.434-.251a.35.35 0 0 0-.35-.607l-.435.251v-.508a.35.35 0 1 0-.699 0v.509l-.435-.251a.35.35 0 0 0-.35.606l.434.25-.44.255a.35.35 0 1 0 .35.606l.441-.255v.503a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606zm-4.739 0-.44-.254.434-.251a.35.35 0 0 0-.35-.607l-.434.251v-.508a.35.35 0 1 0-.699 0v.509l-.436-.251a.35.35 0 0 0-.35.606l.435.25-.44.255a.35.35 0 1 0 .35.606l.441-.255v.503a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606z\"/>\n";
                break;
            case "401":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-3.312 11.38-.44-.254.434-.251a.35.35 0 0 0-.35-.607l-.434.251v-.508a.35.35 0 1 0-.7 0v.509l-.435-.251a.35.35 0 0 0-.35.606l.434.25-.44.255a.35.35 0 1 0 .35.606l.441-.255v.503a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606zm-3.547-1.985-.44-.254.434-.25a.35.35 0 0 0-.35-.607l-.434.25v-.508a.35.35 0 1 0-.7 0v.51l-.435-.252a.35.35 0 0 0-.35.606l.435.25-.44.255a.35.35 0 1 0 .35.606l.44-.254v.502a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606zm7.19 0-.44-.254.434-.25a.35.35 0 0 0-.35-.607l-.434.25v-.508a.35.35 0 1 0-.7 0v.509l-.434-.251a.35.35 0 0 0-.35.606l.434.25-.44.255a.35.35 0 1 0 .35.606l.44-.254v.502a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606z\"/>\n";
                break;
            case "402":
                path = "<path d=\"M3.661 8.455a3.172 3.172 0 0 0 1.244-.253 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116 3.147 3.147 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.147 3.147 0 0 0-.397-.04 3.184 3.184 0 1 0 0 6.369zm4.783 4.893.514-.297a.303.303 0 0 0-.304-.524l-.513.296v-.6a.302.302 0 0 0-.605 0v.601l-.514-.297a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 0 0 .304.525l.52-.3v.593a.302.302 0 0 0 .605 0v-.593l.52.3a.303.303 0 0 0 .303-.525zm-3.579-1.559.514-.297a.303.303 0 0 0-.303-.524l-.514.296v-.6a.302.302 0 0 0-.605 0v.6l-.514-.296a.303.303 0 0 0-.303.524l.514.297-.52.3a.303.303 0 0 0 .303.524l.52-.3v.593a.302.302 0 0 0 .605 0v-.593l.52.3a.303.303 0 0 0 .303-.524zm7.255 0 .514-.297a.303.303 0 0 0-.304-.524l-.513.296v-.6a.302.302 0 1 0-.605 0v.6l-.513-.296a.303.303 0 0 0-.304.524l.514.296-.52.3a.303.303 0 0 0 .303.525l.52-.3v.593a.302.302 0 1 0 .605 0v-.593l.52.3a.303.303 0 0 0 .303-.524zm3.729-1.516-.52-.3.514-.297a.303.303 0 0 0-.304-.524l-.514.297v-.6a.302.302 0 0 0-.605 0v.6l-.513-.297a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 0 0 .304.525l.52-.3v.593a.302.302 0 0 0 .604 0v-.594l.52.3a.303.303 0 0 0 .304-.524zm-13.966-.3.514-.297a.303.303 0 0 0-.304-.524l-.514.297v-.6a.302.302 0 0 0-.605 0v.6L.46 9.151a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 0 0 .304.525l.519-.3v.593a.302.302 0 0 0 .605 0v-.594l.52.3a.303.303 0 0 0 .304-.524z\"/>\n";
                break;
            case "403":
                path = "<path d=\"M3.685 8.455a3.172 3.172 0 0 0 1.243-.253 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116 3.147 3.147 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.147 3.147 0 0 0-.397-.04 3.184 3.184 0 1 0 0 6.369zm4.759 5.929.514-.297a.303.303 0 0 0-.304-.524l-.513.296v-.6a.302.302 0 1 0-.605 0v.6l-.514-.296a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 1 0 .304.524l.52-.3v.593a.302.302 0 1 0 .605 0v-.593l.52.3a.302.302 0 0 0 .413-.11.302.302 0 0 0-.11-.414zM4.865 12.35l.514-.298a.303.303 0 0 0-.303-.524l-.514.297v-.6a.302.302 0 0 0-.605 0v.6l-.514-.297a.303.303 0 0 0-.303.524l.514.297-.52.3a.303.303 0 0 0 .303.525l.52-.3v.593a.302.302 0 0 0 .605 0v-.593l.52.3a.303.303 0 0 0 .303-.524zm7.255 0 .514-.298a.303.303 0 0 0-.304-.524l-.513.297v-.6a.302.302 0 0 0-.605 0v.6l-.513-.297a.303.303 0 0 0-.304.525l.514.296-.52.3a.303.303 0 0 0 .303.525l.52-.3v.593a.302.302 0 1 0 .605 0v-.593l.52.3a.303.303 0 0 0 .303-.524zm-4.233.04a.302.302 0 0 0 .302-.302v-.356l.314.18a.302.302 0 0 0 .413-.11.302.302 0 0 0-.11-.414l-.313-.18.309-.179a.303.303 0 0 0-.303-.524l-.31.179v-.363a.302.302 0 0 0-.605 0v.362l-.308-.178a.303.303 0 0 0-.304.524l.309.178-.314.181a.303.303 0 1 0 .304.525l.313-.181v.356a.302.302 0 0 0 .303.303zm7.962-1.983-.52-.3.514-.297a.303.303 0 0 0-.304-.524l-.514.297v-.601a.302.302 0 0 0-.605 0v.6l-.513-.296a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 0 0 .304.524l.52-.3v.593a.302.302 0 0 0 .604 0v-.593l.52.3a.303.303 0 0 0 .304-.524zm-13.966-.3.514-.297a.303.303 0 0 0-.304-.524l-.514.297v-.601a.302.302 0 0 0-.605 0v.6L.46 9.286a.303.303 0 0 0-.304.524l.514.297-.52.3a.303.303 0 0 0 .304.524l.519-.3v.593a.302.302 0 0 0 .605 0v-.593l.52.3a.303.303 0 0 0 .304-.524z\"/>\n";
                break;
            case "404":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-7.79 10.623a1 1 0 1 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zm6.935-.113-.664-.383.658-.38a.35.35 0 0 0-.35-.606l-.658.38v-.768a.35.35 0 1 0-.7 0v.768l-.657-.38a.35.35 0 0 0-.35.606l.657.38-.664.383a.35.35 0 1 0 .35.607l.665-.384v.76a.35.35 0 1 0 .699 0v-.76l.665.384a.35.35 0 0 0 .35-.607z\"/>\n";
                break;
            case "405":
                path = "<path d=\"M8.407 9.998a.499.499 0 0 0-.626.33l-1.085 3.478a.5.5 0 0 0 .33.626.489.489 0 0 0 .148.022.5.5 0 0 0 .477-.351l1.085-3.479a.5.5 0 0 0-.329-.626zm3.908-7.912a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.185 3.185 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm-8.79 10.623a1 1 0 0 0 2 0 6.603 6.603 0 0 0-1-2 6.603 6.603 0 0 0-1 2zm8.935-.112-.665-.384.658-.38a.35.35 0 0 0-.35-.605l-.657.379v-.768a.35.35 0 0 0-.699 0v.768l-.658-.38a.35.35 0 0 0-.35.606l.658.38-.666.384a.35.35 0 0 0 .175.652.343.343 0 0 0 .175-.047l.666-.384v.76a.35.35 0 0 0 .7 0v-.76l.664.384a.343.343 0 0 0 .175.047.35.35 0 0 0 .175-.652z\"/>\n";
            break;
            case "406":
                path = "<path d=\"M4.436 14.968a1 1 0 0 0 2 0 6.604 6.604 0 0 0-1-2 6.604 6.604 0 0 0-1 2zm6.827-.272-.557-.322.551-.318a.35.35 0 0 0-.35-.606l-.55.319v-.645a.35.35 0 1 0-.7 0v.645l-.552-.319a.35.35 0 0 0-.35.606l.552.319-.557.321a.35.35 0 1 0 .35.607l.557-.322v.636a.35.35 0 1 0 .7 0v-.636l.557.322a.35.35 0 0 0 .35-.607zM5.195 1.904h.007a.5.5 0 0 0 .493-.506L5.683.494a.5.5 0 0 0-.5-.494h-.007a.5.5 0 0 0-.493.506l.012.905a.5.5 0 0 0 .5.493zm-2.892.946a.5.5 0 1 0 .698-.716l-.648-.63a.5.5 0 1 0-.697.715zm-.179 2.203a.5.5 0 0 0-.5-.493h-.007l-.904.011a.5.5 0 0 0 .006 1h.007l.905-.011a.5.5 0 0 0 .493-.507zm5.638-2.12a.5.5 0 0 0 .359-.151l.63-.648a.5.5 0 0 0-.716-.698l-.631.647a.5.5 0 0 0 .358.85zm4.254 2.646a2.938 2.938 0 0 0-.37.037 4.364 4.364 0 0 0-7.315 0 2.937 2.937 0 0 0-.37-.037 2.972 2.972 0 1 0 1.16 5.709 4.302 4.302 0 0 0 5.734 0 2.972 2.972 0 1 0 1.161-5.71zM2.47 5.315a3.53 3.53 0 0 1 1.018-.288 1.831 1.831 0 0 1 1.811-1.603 1.809 1.809 0 0 1 .553.095 4.926 4.926 0 0 1 1.282-.405 2.82 2.82 0 0 0-4.67 2.145c0 .02.006.037.006.056z\"/>\n";
                break;
            case "407":
                path = "<path d=\"M5.241 1.904h.007a.5.5 0 0 0 .493-.506L5.73.494A.5.5 0 0 0 5.23 0h-.007a.5.5 0 0 0-.493.506l.011.905a.5.5 0 0 0 .5.493zM2.35 2.85a.5.5 0 1 0 .697-.716l-.647-.63a.5.5 0 1 0-.698.715zm-.18 2.203a.5.5 0 0 0-.5-.493h-.007l-.903.01a.5.5 0 0 0 .007 1h.007l.904-.011a.5.5 0 0 0 .493-.507zm5.639-2.12a.5.5 0 0 0 .358-.151l.631-.648a.5.5 0 0 0-.717-.698l-.63.647a.5.5 0 0 0 .358.85zm4.253 2.646a2.938 2.938 0 0 0-.37.037 4.364 4.364 0 0 0-7.315 0 2.937 2.937 0 0 0-.37-.037 2.972 2.972 0 1 0 1.16 5.709 4.302 4.302 0 0 0 5.735 0 2.972 2.972 0 1 0 1.16-5.71zm-9.546-.264a3.53 3.53 0 0 1 1.018-.288 1.831 1.831 0 0 1 1.812-1.603 1.809 1.809 0 0 1 .552.095 4.926 4.926 0 0 1 1.282-.405A2.82 2.82 0 0 0 2.51 5.26c0 .02.006.037.006.056zm8.738 9.481-.44-.254.434-.251a.35.35 0 0 0-.35-.606l-.435.25v-.508a.35.35 0 1 0-.699 0v.509l-.435-.251a.35.35 0 0 0-.35.606l.434.25-.44.255a.35.35 0 1 0 .35.606l.441-.255v.503a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606zm-4.739 0-.44-.254.435-.251a.35.35 0 0 0-.35-.606l-.435.25v-.508a.35.35 0 1 0-.699 0v.509l-.435-.251a.35.35 0 0 0-.35.606l.434.25-.44.255a.35.35 0 1 0 .35.606l.441-.255v.503a.35.35 0 1 0 .7 0v-.502l.44.254a.35.35 0 0 0 .35-.606z\"/>\n";
                break;
            case "408":
                path = "<path d=\"m12.035 11.946.51-.294a.3.3 0 0 0-.301-.52l-.509.295v-.595a.3.3 0 1 0-.6 0v.595l-.51-.294a.3.3 0 0 0-.3.52l.51.293-.516.298a.3.3 0 0 0 .3.52l.516-.298v.588a.3.3 0 0 0 .6 0v-.588l.515.298a.3.3 0 0 0 .3-.52zm-7.25 0 .306-.177a.3.3 0 0 0-.3-.52l-.307.177v-.359a.3.3 0 0 0-.6 0v.359l-.305-.177a.3.3 0 0 0-.301.52l.306.177-.31.179a.3.3 0 0 0 .15.56.303.303 0 0 0 .15-.04l.31-.18v.354a.3.3 0 0 0 .6 0v-.354l.31.18a.3.3 0 0 0 .41-.11.3.3 0 0 0-.109-.41zm7.53-9.86a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zM8.441 10.15a.501.501 0 0 0-.626.329L6.73 13.957a.5.5 0 0 0 .955.298l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "409":
                path = "<path d=\"m11.035 11.946.51-.294a.3.3 0 0 0-.301-.52l-.509.295v-.595a.3.3 0 1 0-.6 0v.595l-.51-.294a.3.3 0 0 0-.3.52l.51.293-.516.298a.3.3 0 0 0 .3.52l.516-.298v.588a.3.3 0 0 0 .6 0v-.588l.515.298a.3.3 0 0 0 .3-.52zm-7.25 0 .306-.177a.3.3 0 0 0-.3-.52l-.307.177v-.359a.3.3 0 0 0-.6 0v.359l-.305-.177a.3.3 0 0 0-.301.52l.306.177-.31.179a.3.3 0 0 0 .15.56.303.303 0 0 0 .15-.04l.31-.18v.354a.3.3 0 0 0 .6 0v-.354l.31.18a.3.3 0 0 0 .41-.11.3.3 0 0 0-.109-.41zM14.29 10.63l.306-.176a.3.3 0 0 0-.3-.52l-.306.177v-.359a.3.3 0 1 0-.6 0v.36l-.306-.178a.3.3 0 0 0-.301.52l.306.177-.31.179a.3.3 0 0 0 .15.56.303.303 0 0 0 .15-.04l.311-.18v.353a.3.3 0 0 0 .6 0v-.353l.31.18a.3.3 0 0 0 .301-.52zm1.21-5.36a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zm-8.059 4.88a.502.502 0 0 0-.626.329L5.73 13.957a.5.5 0 0 0 .955.298l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "410":
                path = "<path d=\"m11.144 11.71.51-.294a.3.3 0 0 0-.3-.52l-.51.295v-.595a.3.3 0 1 0-.6 0v.595l-.509-.294a.3.3 0 0 0-.3.52l.509.294-.516.297a.3.3 0 0 0 .301.52l.516-.298v.589a.3.3 0 0 0 .6 0v-.588l.514.297a.3.3 0 0 0 .3-.52zm3.44-1.309.51-.294a.3.3 0 0 0-.301-.52l-.51.295v-.595a.3.3 0 1 0-.6 0v.595l-.509-.294a.3.3 0 0 0-.3.52l.51.293-.516.298a.3.3 0 0 0 .3.52l.515-.297v.587a.3.3 0 0 0 .6 0v-.588l.516.298a.3.3 0 0 0 .3-.52zm-11.861.433.306-.176a.3.3 0 0 0-.3-.52l-.306.177v-.36a.3.3 0 0 0-.6 0v.36l-.306-.177a.3.3 0 0 0-.3.52l.306.176-.311.18a.3.3 0 0 0 .15.56.303.303 0 0 0 .15-.04l.31-.18v.353a.3.3 0 0 0 .6 0v-.353l.311.18a.3.3 0 0 0 .41-.11.3.3 0 0 0-.11-.41zm2.615 1.536.306-.177a.3.3 0 0 0-.3-.52l-.307.178v-.36a.3.3 0 1 0-.6 0v.36l-.305-.177a.3.3 0 0 0-.301.52l.306.176-.31.18a.3.3 0 0 0 .15.56.303.303 0 0 0 .15-.041l.31-.179v.353a.3.3 0 1 0 .6 0v-.354l.31.18a.3.3 0 0 0 .41-.11.3.3 0 0 0-.109-.41zM15.5 5.27a3.184 3.184 0 0 0-3.185-3.184 3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0A3.185 3.185 0 0 0 15.5 5.27zm-7.473 4.88a.501.501 0 0 0-.626.329l-1.085 3.478a.5.5 0 0 0 .955.298l1.085-3.478a.5.5 0 0 0-.329-.627z\"/>\n";
                break;
            case "499":
                path = "<path d=\"M14.483 9.172a.504.504 0 0 0-.612-.354l-2.233.599-.98-.566a2.655 2.655 0 0 0-.056-1.884l1.022-.59 2.108.565a.542.542 0 0 0 .13.017.5.5 0 0 0 .13-.983l-1.143-.306.809-.467a.5.5 0 1 0-.5-.865l-.886.512.338-1.265a.501.501 0 0 0-.353-.613.508.508 0 0 0-.612.353l-.598 2.232-.979.564a2.782 2.782 0 0 0-1.661-.884V4.05l1.542-1.542a.5.5 0 0 0-.707-.707l-.835.835v-.933a.5.5 0 1 0-1 0v1.023L6.48 1.8a.5.5 0 1 0-.707.707l1.633 1.632v1.123a2.791 2.791 0 0 0-1.595 1.005l-1.03-.595-.565-2.108a.5.5 0 1 0-.966.26l.306 1.141L2.75 4.5a.5.5 0 1 0-.5.865l.886.512-1.265.339A.5.5 0 0 0 2 7.198a.541.541 0 0 0 .13-.016l2.232-.599.98.566a2.655 2.655 0 0 0 .056 1.884l-1.022.59-2.108-.565a.507.507 0 0 0-.613.353.501.501 0 0 0 .354.613l1.142.306-.809.467a.5.5 0 0 0 .25.932.493.493 0 0 0 .25-.067l.886-.512-.338 1.265a.501.501 0 0 0 .353.613.542.542 0 0 0 .13.017.5.5 0 0 0 .482-.37l.598-2.232.979-.564a2.782 2.782 0 0 0 1.661.884v1.188l-1.542 1.542a.5.5 0 0 0 .707.707l.835-.835v.933a.5.5 0 0 0 1 0v-1.023l.926.925a.5.5 0 0 0 .707-.707l-1.633-1.632v-1.123a2.791 2.791 0 0 0 1.595-1.005l1.03.595.565 2.108a.5.5 0 0 0 .482.37.541.541 0 0 0 .13-.017.501.501 0 0 0 .354-.613l-.306-1.141.807.466a.493.493 0 0 0 .25.067.5.5 0 0 0 .25-.932l-.886-.512 1.265-.339a.501.501 0 0 0 .354-.613z\"/>\n";
                break;
            case "500":
                path = "<path d=\"M7.031 9.208a.5.5 0 0 1 0-1h8.45a3.05 3.05 0 0 0-3.543-2.77 4.675 4.675 0 0 0-7.838 0 3.149 3.149 0 0 0-.397-.04 3.184 3.184 0 1 0 1.244 6.117 4.61 4.61 0 0 0 6.144 0 3.184 3.184 0 0 0 4.365-2.307z\"/>\n";
                break;
            case "501":
                path = "<path d=\"M12.315 3.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.146 3.146 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm1.187 8.214H4.525a.5.5 0 0 0 0 1h8.977a.5.5 0 0 0 0-1zm-2.518 2.313a.5.5 0 0 0-.5-.5H1.508a.5.5 0 0 0 0 1h8.976a.5.5 0 0 0 .5-.5z\"/>\n";
                break;
            case "502":
                path = "<path d=\"M12.543 4.487c-1.581 0-3.876 1.712-4.57 2.888C7.282 6.2 4.987 4.487 3.406 4.487a3.486 3.486 0 0 0 0 6.97c1.58 0 3.876-1.75 4.569-2.906.693 1.156 2.988 2.906 4.569 2.906a3.486 3.486 0 0 0 0-6.97z\"/>\n";
                break;
            case "503":
                path = "<circle cx=\"5.332\" cy=\"7.142\" r=\".581\"/>\n" +
                        "  <path d=\"M4.156 3.654a.581.581 0 1 0-.581-.582.58.58 0 0 0 .58.582zm6.081 2.674a.581.581 0 1 0-.581-.581.581.581 0 0 0 .581.58zM8.97 3.654a.581.581 0 1 0-.582-.582.582.582 0 0 0 .581.582z\"/>\n" +
                        "  <circle cx=\"2.412\" cy=\"5.398\" r=\".581\"/>\n" +
                        "  <path d=\"M9.507 10.61a.581.581 0 1 0-.76.317.581.581 0 0 0 .76-.316zm-3.637.218a.581.581 0 1 0-.76.317.581.581 0 0 0 .76-.317zm.845 2.564a.581.581 0 1 0 .316.759.581.581 0 0 0-.316-.76zm.36-7.878a.582.582 0 1 0-.581-.581.582.582 0 0 0 .581.581z\"/>\n" +
                        "  <path d=\"M14.438 6.226a2.869 2.869 0 0 0 .239-.984c.13-2.698-3.196-3.84-3.337-3.888a.45.45 0 0 0-.286.854c.028.009 2.821.974 2.724 2.99a2.26 2.26 0 0 1-.863 1.612C11.232 8.268 7.249 8.932 2.77 8.505a.458.458 0 0 0-.492.404.45.45 0 0 0 .406.491c.943.09 1.86.134 2.74.134 3.59 0 6.563-.73 8.08-2.044.01-.008.016-.018.026-.026a5.517 5.517 0 0 1-.168 1.159c-.571 1.837-3.716 3.541-8.41 3.097a.458.458 0 0 0-.491.405.45.45 0 0 0 .405.49c.55.054 1.078.079 1.587.079 3.117 0 5.432-.943 6.727-2.211a3.823 3.823 0 0 1-.176 1.013c-.25.708-1.35 2.996-4.901 2.723a.432.432 0 0 0-.483.415.45.45 0 0 0 .414.483q.321.024.622.024a5.2 5.2 0 0 0 5.196-3.345 6.914 6.914 0 0 0 .205-2.53l-.003-.007a3.356 3.356 0 0 0 .168-.369 10.64 10.64 0 0 0 .234-2.57.52.52 0 0 0-.018-.094z\"/>\n";
                break;
            case "504":
                path = "<defs>\n" +
                        "    <style>\n" +
                        "      .cls-1{fill-rule:evenodd}\n" +
                        "    </style>\n" +
                        "  </defs>\n" +
                        "  <path d=\"M9.712 2.908c1.786.733 2.906 1.784 2.906 2.953 0 2.213-4.008 4.007-8.953 4.007a18.977 18.977 0 0 1-3.299-.282 16.37 16.37 0 0 0 6.048 1.052 16.768 16.768 0 0 0 5.719-.924 5.344 5.344 0 0 0 1.341-.62c1.164-.716 1.892-1.534 1.892-2.462 0-1.69-2.342-3.136-5.654-3.724z\" class=\"cls-1\"/>\n" +
                        "  <path d=\"M5.285 7.363a.988.988 0 1 0-.987-.988.988.988 0 0 0 .987.988zm3.856-1.455a.988.988 0 1 0-.987-.988.988.988 0 0 0 .987.988zm1.156 5.462a.988.988 0 1 0 .988.987.987.987 0 0 0-.988-.988zm-2.54 1.24a.597.597 0 1 0 .597.597.597.597 0 0 0-.597-.597zm4.743-1.902a.597.597 0 1 0 .598.597.597.597 0 0 0-.597-.597zM3.533 8.46a.637.637 0 1 0-.636-.637.637.637 0 0 0 .636.637zm3.792-4.528a.541.541 0 1 0-.541-.542.541.541 0 0 0 .541.542zm-.04 3.073a.636.636 0 1 0 .637-.637.636.636 0 0 0-.637.637z\" class=\"cls-1\"/>\n";
                break;
            case "507":
                path = "<path d=\"M12.565 8.867a.028.028 0 0 0 .048.018l1.593-1.462a.074.074 0 0 0 0-.11L12.599 5.84a.027.027 0 0 0-.045.017l.003.654H5.863c-1.106-.706-1.54-1.5-1.52-2.126.034-.988 1.058-1.819 2.552-2.069a3.433 3.433 0 0 1 3.68 1.43.7.7 0 0 0 1.157-.788A4.818 4.818 0 0 0 6.664.936C4.494 1.299 3 2.667 2.945 4.339a3.231 3.231 0 0 0 .782 2.172H2.72a.888.888 0 1 0 0 1.775h3.506a13.08 13.08 0 0 0 2.313.766c2.255.532 3.115 1.555 3.168 2.336.05.742-.565 1.47-1.567 1.854a5.479 5.479 0 0 1-5.503-.75.7.7 0 1 0-.928 1.049 6.697 6.697 0 0 0 4.263 1.523 7.402 7.402 0 0 0 2.67-.515 3.413 3.413 0 0 0 2.462-3.255 3.798 3.798 0 0 0-2.497-3.008h1.957z\"/>\n";
                break;
            case "508":
                path = "<path d=\"M12.495 7.754a.029.029 0 0 0 .048.018l1.593-1.461a.074.074 0 0 0 0-.11l-1.607-1.474a.027.027 0 0 0-.045.018l.003.654H4.499a1.795 1.795 0 0 1-.323-.996c.032-.987 1.057-1.818 2.55-2.068a3.45 3.45 0 0 1 3.68 1.43.7.7 0 0 0 1.158-.79A4.825 4.825 0 0 0 6.495.956c-2.169.362-3.663 1.73-3.718 3.402A3.006 3.006 0 0 0 2.949 5.4h-.3a.888.888 0 0 0 0 1.775h1.544A10.022 10.022 0 0 0 8.37 9.07c.187.044.35.096.517.146H2.649a.888.888 0 0 0 0 1.776h8.78a1.372 1.372 0 0 1 .108.413c.05.742-.565 1.47-1.567 1.856a5.48 5.48 0 0 1-5.502-.75.7.7 0 0 0-.928 1.049 6.696 6.696 0 0 0 4.262 1.523 7.396 7.396 0 0 0 2.67-.516 3.413 3.413 0 0 0 2.462-3.256c-.001-.024-.01-.045-.012-.068l1.214-1.114a.074.074 0 0 0 0-.11l-1.607-1.474a.027.027 0 0 0-.045.017l.003.654h-.659a7.045 7.045 0 0 0-3.137-1.509A11.943 11.943 0 0 1 7 7.174h5.493z\"/>\n";
                break;
            case "509":
                path = "<path d=\"M12.315 2.086a3.146 3.146 0 0 0-.396.04 4.675 4.675 0 0 0-7.838 0 3.149 3.149 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.185 3.185 0 1 0 1.243-6.116zm1.186 8.214H4.525a.5.5 0 0 0 0 1h8.976a.5.5 0 0 0 0-1zm-2.517 2.313a.5.5 0 0 0-.5-.5H1.508a.5.5 0 1 0 0 1h8.976a.5.5 0 0 0 .5-.5zm1.11 1.344H3.118a.5.5 0 0 0 0 1h8.976a.5.5 0 0 0 0-1z\"/>\n";
                break;
            case "510":
                path = "<path d=\"M6.47 5.396a.5.5 0 0 1 .5-.5h8.45a3.05 3.05 0 0 0-3.544-2.77 4.675 4.675 0 0 0-7.838 0 3.149 3.149 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.184 3.184 0 0 0 4.365-2.306H6.97a.5.5 0 0 1-.5-.5zm7.031 4.904H4.525a.5.5 0 0 0 0 1h8.976a.5.5 0 0 0 0-1zm-2.517 2.313a.5.5 0 0 0-.5-.5H1.508a.5.5 0 1 0 0 1h8.976a.5.5 0 0 0 .5-.5zm1.11 1.344H3.118a.5.5 0 0 0 0 1h8.976a.5.5 0 0 0 0-1z\"/>\n";
                break;
            case "511":
                path = "<path d=\"M12.543 4.487c-1.581 0-3.876 1.712-4.57 2.888C7.282 6.2 4.987 4.487 3.406 4.487a3.486 3.486 0 0 0 0 6.97c1.58 0 3.876-1.75 4.569-2.906.693 1.156 2.988 2.906 4.569 2.906a3.486 3.486 0 0 0 0-6.97z\"/>\n" +
                        "  <circle cx=\"2.989\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <circle cx=\"13\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <circle cx=\"2.989\" cy=\"13.457\" r=\"1\"/>\n" +
                        "  <circle cx=\"13\" cy=\"13.457\" r=\"1\"/>\n";
                break;
            case "512":
                path = "<circle cx=\"2.989\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <path d=\"M12.543 4.487c-1.581 0-3.876 1.712-4.57 2.888C7.282 6.2 4.987 4.487 3.406 4.487a3.486 3.486 0 0 0 0 6.97c1.58 0 3.876-1.75 4.569-2.906.693 1.156 2.988 2.906 4.569 2.906a3.486 3.486 0 0 0 0-6.97z\"/>\n" +
                        "  <circle cx=\"8.031\" cy=\"2.958\" r=\"1\"/>\n" +
                        "  <circle cx=\"8.031\" cy=\"12.98\" r=\"1\"/>\n" +
                        "  <circle cx=\"13\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <circle cx=\"2.989\" cy=\"13.457\" r=\"1\"/>\n" +
                        "  <circle cx=\"13\" cy=\"13.457\" r=\"1\"/>\n";
                break;
            case "513":
                path = "<circle cx=\"1.989\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <path d=\"M15.947 7.972a3.445 3.445 0 0 0-3.404-3.485c-1.581 0-3.876 1.712-4.57 2.888C7.282 6.2 4.987 4.487 3.406 4.487a3.486 3.486 0 0 0 0 6.97c1.58 0 3.876-1.75 4.569-2.906.693 1.156 2.988 2.906 4.569 2.906a3.445 3.445 0 0 0 3.404-3.485z\"/>\n" +
                        "  <circle cx=\"6.031\" cy=\"2.958\" r=\"1\"/>\n" +
                        "  <circle cx=\"10.076\" cy=\"2.958\" r=\"1\"/>\n" +
                        "  <circle cx=\"6.031\" cy=\"12.987\" r=\"1\"/>\n" +
                        "  <circle cx=\"10.076\" cy=\"12.987\" r=\"1\"/>\n" +
                        "  <circle cx=\"14\" cy=\"2.481\" r=\"1\"/>\n" +
                        "  <circle cx=\"1.989\" cy=\"13.457\" r=\"1\"/>\n" +
                        "  <circle cx=\"14\" cy=\"13.457\" r=\"1\"/>\n";
                break;
            case "514":
                path = "<path d=\"M13.502 11.3H4.525a.5.5 0 0 1 0-1h8.977a.5.5 0 0 1 0 1zm-3.019 1.813H1.508a.5.5 0 1 1 0-1h8.975a.5.5 0 0 1 0 1zm1.611 1.844H3.118a.5.5 0 1 1 0-1h8.976a.5.5 0 0 1 0 1zM6.047 7.173a.502.502 0 1 1 0-1h8.807A3.15 3.15 0 0 0 15 5.27a3.187 3.187 0 0 0-.059-.583H8.394a.5.5 0 0 1 0-1h6.17a3.17 3.17 0 0 0-2.748-1.601 3.146 3.146 0 0 0-.397.04 4.675 4.675 0 0 0-7.838 0 3.149 3.149 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116 4.61 4.61 0 0 0 6.144 0 3.166 3.166 0 0 0 3.783-1.029z\"/>\n";
                break;
            case "515":
                path = "<path d=\"M4.428 8.202a4.61 4.61 0 0 0 6.144 0 3.166 3.166 0 0 0 3.783-1.029H6.047a.502.502 0 1 1 0-1h8.807A3.15 3.15 0 0 0 15 5.27a3.187 3.187 0 0 0-.059-.583H8.394a.5.5 0 0 1 0-1h6.17a3.17 3.17 0 0 0-2.748-1.601 3.146 3.146 0 0 0-.397.04 4.675 4.675 0 0 0-7.838 0 3.149 3.149 0 0 0-.396-.04 3.184 3.184 0 1 0 1.243 6.116zM14.502 10.3H7.525a.5.5 0 0 0 0 1h6.977a.5.5 0 0 0 0-1zm-10.496 0a.5.5 0 0 0 0 1h1.99a.5.5 0 1 0 0-1zm8.807 2.813a.5.5 0 1 0 0-1h-1.989a.5.5 0 0 0 0 1zm-8.316.813h-1.99a.5.5 0 1 0 0 1h1.99a.5.5 0 1 0 0-1zm5.486-1.313a.5.5 0 0 0-.5-.5H1.508a.5.5 0 1 0 0 1h7.975a.5.5 0 0 0 .5-.5zm3.111 1.344H6.118a.5.5 0 0 0 0 1h6.976a.5.5 0 0 0 0-1z\"/>\n";
                break;

        }
        return path;
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                String[] filePaths = file.list();
                for (String path : filePaths) {
                    deleteFile(filePath + File.separator + path);
                }
                file.delete();
            }
        }
    }

    public static Activity getActivityContext(Context context) {
        if (context == null)
            return null;
        else if (context instanceof Activity)
            return (Activity) context;
        else if (context instanceof TintContextWrapper)
            return scanForActivity(((TintContextWrapper) context).getBaseContext());
        else if (context instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) context).getBaseContext());

        return null;
    }


    public static boolean getCurrentScreenLand(Activity context) {
        return context.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 ||
                context.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270;

    }

    public static String parseNetSpeed(long downloadSpeed) {
        String speed = "";
        if(downloadSpeed > 999999999){
            speed = downloadSpeed/1000/1000/1000 + "GB/S";
        }else if(downloadSpeed > 999999){
            speed = downloadSpeed/1000/1000 + "MB/S";
        }else if(downloadSpeed > 999){
            speed = downloadSpeed/1000 + "KB/S";
        }else{
            speed = downloadSpeed + "B/S";
        }

        return speed;
    }

    public static double parseDoubleCount(double vx) {
        double v = vx;
        if(String.valueOf(v).length() > 5){
            try{
                v = Double.parseDouble(String.valueOf(v).substring(0,5));
            }catch (Exception e){
                //
            }
        }
        return v;
    }

    public static long times = 0;
    ///点击事件封装-点击
    public static void click(View view,Action action) {
        final boolean[] force = {false};//防止连续点击300ms
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long now = new Date().getTime();
                        if(now - times < 300){
                            force[0] = true;
                            return true;
                        }
                        force[0] = false;
                        times = now;
                        CommonUtil.applyClickDownAnimation(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(force[0]){
                            return true;
                        }
                        times = new Date().getTime();
                        CommonUtil.applyClickUpAnimation(view);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                action.click();
                            }
                        },100);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(force[0]){
                            return true;
                        }
                        times = new Date().getTime();
                        CommonUtil.applyClickUpAnimation(view);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    ///点击事件封装-按下抬起
    public static void clickDownUp(View view,ActionDownUp action) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        CommonUtil.applyClickDownAnimation(view);
                        action.clickDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        CommonUtil.applyClickUpAnimation(view);
                        action.clickUp();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CommonUtil.applyClickUpAnimation(view);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    //按下缩小
    public static void applyClickDownAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.93f,  // 从正常缩放到缩小
                1.0f, 0.93f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100); // 动画时长
        scaleAnimation.setFillAfter(true); // 保持缩放后的状态
        view.startAnimation(scaleAnimation);
    }
    //松手放大
    public static void applyClickUpAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.93f, 1.0f,  // 从缩小恢复到原始大小
                0.93f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100); // 动画时长
        scaleAnimation.setFillAfter(false); //保持最终状态（还原）防止setVisibility()失效
        view.startAnimation(scaleAnimation);
    }

    //背景切换动画
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void applyFancyAnimation(View view) {
        // **1. 背景颜色渐变**
        Drawable[] layers;
        layers = new Drawable[]{
                ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_gray),
                ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_blue)
        };

        // 创建背景渐变效果的 TransitionDrawable
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(500); // 背景切换渐变 500ms
        transitionDrawable.setCrossFadeEnabled(true);
        new Handler().postDelayed(() -> {
            view.setBackground(ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_blue));
        }, 700);

        // **2. 按钮点击时的缩放动画（轻微缩小再弹回）**
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f, 1f);

        // **3. 旋转动画（轻微旋转增加动感）**
        //PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 2f, -2f, 0f);

        // **4. 透明度动画（微闪效果）**
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.6f, 1f);

        // 组合动画
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, /*rotation,*/ alpha);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // 平滑过渡
        animator.setDuration(600);
        animator.start();
    }

    //背景切换动画
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void applyFancyBackAnimation(View view) {
        // **1. 背景颜色渐变**
        Drawable[] layers;
        layers = new Drawable[]{
                ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_blue),
                ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_gray)
        };

        // 创建背景渐变效果的 TransitionDrawable
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(500); // 背景切换渐变 500ms
        new Handler().postDelayed(() -> {
            view.setBackground(ContextCompat.getDrawable(HhApplication.getInstance(), R.drawable.circle_gray));
        }, 700);

        // **2. 按钮点击时的缩放动画（轻微缩小再弹回）**
        //PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f, 1f);
        //PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f, 1f);

        // **3. 旋转动画（轻微旋转增加动感）**
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 2f, -2f, 0f);

        // **4. 透明度动画（微闪效果）**
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.6f, 1f);

        // 组合动画
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, /*scaleX, scaleY,*/ rotation, alpha);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // 平滑过渡
        animator.setDuration(600);
        animator.start();
    }

    public static void showConfirm(Context context,String message,String confirmText,String cancelText,Action confirmClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogNoBackground);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_message, null);
        builder.setView(customView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        TextView confirmButton = customView.findViewById(R.id.cancel);
        TextView cancelButton = customView.findViewById(R.id.confirm);
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);
        CommonUtil.click(confirmButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
                confirmClick.click();
            }
        });
        CommonUtil.click(cancelButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public static void showConfirm(Context context,String message,String confirmText,String cancelText,Action confirmClick,Action cancelClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogNoBackground);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_message, null);
        builder.setView(customView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        TextView confirmButton = customView.findViewById(R.id.cancel);
        TextView cancelButton = customView.findViewById(R.id.confirm);
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);
        CommonUtil.click(confirmButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
                confirmClick.click();
            }
        });
        CommonUtil.click(cancelButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
                cancelClick.click();
            }
        });
        dialog.show();
    }

    public static void showConfirm(Context context,String message,String confirmText,String cancelText,Action confirmClick,Action cancelClick,Boolean delete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogNoBackground);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_message, null);
        builder.setView(customView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        TextView messageView = customView.findViewById(R.id.message);
        TextView confirmButton = customView.findViewById(R.id.confirm);
        TextView cancelButton = customView.findViewById(R.id.cancel);
        messageView.setText(message);
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);
        if(delete){
            confirmButton.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_red_4));
        }else{
            confirmButton.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_blue_4));
        }
        CommonUtil.click(confirmButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
                confirmClick.click();
            }
        });
        CommonUtil.click(cancelButton, new Action() {
            @Override
            public void click() {
                dialog.cancel();
                cancelClick.click();
            }
        });
        dialog.show();
    }
}