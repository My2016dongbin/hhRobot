package com.ehaohai.robot.helper;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehaohai.robot.R;


public class DialogHelper {

    private TextView tvLoading;

    public static DialogHelper getInstance() {
        return LoadDialogHolder.instance;
    }

    private static class LoadDialogHolder {
        static DialogHelper instance = new DialogHelper();
    }

    /**
     * 展示加载框
     *
     * @param context context
     * @param msg     加载信息
     */
    public void show(Context context, String msg) {
        close();
        createDialog(context, msg);
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 更新提示框信息
     *
     * @param msg
     */
    public void updateMsg(String msg) {
        if (loadingDialog == null || !loadingDialog.isShowing() || tvLoading == null || msg == null)
            return;
        tvLoading.setVisibility(TextUtils.isEmpty(msg) ? View.GONE : View.VISIBLE);
        tvLoading.setText(msg);
    }

    /**
     * 关闭加载框
     */
    public void close() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * progressDialog
     */
    private Dialog loadingDialog;

    /**
     * 创建加载框
     *
     * @param context context
     * @param msg     msg
     */
    private void createDialog(Context context, String msg) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View mView = mInflater.inflate(R.layout.dialog_loading, null);
        tvLoading = mView.findViewById(R.id.tv_loading);
        if (TextUtils.isEmpty(msg)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(msg);
        }
        loadingDialog = new Dialog(context, R.style.LoadingDialogStyle);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(true);
        loadingDialog.setContentView(mView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
      //  window.setWindowAnimations(R.style.SiTuPopWindowAnimStyle);
    }
}

