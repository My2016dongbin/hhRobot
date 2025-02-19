package com.ehaohai.robot.utils;

import android.text.TextUtils;
import android.util.Log;


import com.ehaohai.robot.helper.Constants;

import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.WARN;

public class HhLog {
    private static final int LINE_LIMIT = 1024;

    private static final String TAG = "dancer";


    public static void i(String message) {
        i(TAG, message);
    }

    public static void i(String tag, String message) {
        log(INFO, tag, message);
    }

    public static void w(String message) {
        w(TAG, message);
    }

    public static void w(String tag, String message) {
        log(WARN, tag, message);
    }

    public static void e(String message) {
        e(TAG, message);
    }

    public static void e(String tag, String message) {
        log(ERROR, tag, message);
    }

    private static void log(int type, String tag, String message) {
        if (!Constants.LOG_ENABLE) {
            return;
        }
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message)) {
            return;
        }
        if (message.length() <= LINE_LIMIT) {
            realLog(type, tag, message);
        } else {
            while (message.length() > LINE_LIMIT) {
                String content = message.substring(0, LINE_LIMIT);
                message = message.replace(content, "");
                realLog(type, tag, content);
            }
            realLog(type, tag, message);
        }

    }

    private static void realLog(int type, String tag, String message) {
        switch (type) {
            case INFO:
                Log.i(tag, message);
                break;
            case WARN:
                Log.w(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
            default:
                Log.i(tag, message);
                break;
        }
    }
}
