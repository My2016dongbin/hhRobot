package com.ehaohai.robot.utils;

import android.app.Activity;
import android.view.Gravity;

import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;

public class HhToast {
    private static XToast mToast;

    private HhToast() {
    }

    public static XToast make(Activity activity, String text, int duration) {
        mToast = new XToast(activity)
                .setView(ToastUtils.getToast().getView())
                .setText(android.R.id.message, text)
                .setDuration(duration)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setGravity(Gravity.BOTTOM)
                .setYOffset(100);
        return mToast;
    }

    public static XToast make(Activity activity, String text) {
        mToast = new XToast(activity)
                .setView(ToastUtils.getToast().getView())
                .setText(android.R.id.message, text)
                .setDuration(2000)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setGravity(Gravity.BOTTOM)
                .setYOffset(100);
        return mToast;
    }


    public static void cancel() {
        if (mToast != null)
            mToast.cancel();
    }
}
