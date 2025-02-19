package com.ehaohai.robot.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.ehaohai.robot.helper.DialogHelper;
import com.ehaohai.robot.utils.HhLog;


public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends Fragment {
    protected T binding;

    public static BaseFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        BaseFragment fragment = new BaseFragment() {
            @Override
            protected void setupViewModel() {

            }

            @Override
            public int bindLayoutId() {
                return 0;
            }

            @Override
            public BaseViewModel obtainViewModel() {
                return null;
            }
        };
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, bindLayoutId(), container, false);

        setupViewModel();
        subscribeObserver();
        return binding.getRoot();
    }

    /**
     * 注册ViewModel监听
     */
    protected void subscribeObserver() {
        obtainViewModel().msg.observe(getActivity(), msg -> {
            //关闭软键盘
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(binding.getRoot().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            Log.e("TAG", "subscribeObserver: " + msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });
        //token失效
        obtainViewModel().loading.observe(getActivity(), dialogEvent -> {
            if (dialogEvent.isShow()) {
                DialogHelper.getInstance().show(getActivity(), dialogEvent.getHint());
            } else {
                DialogHelper.getInstance().close();
            }
        });
    }

    /**
     * 打开/关闭输入法
     */
    protected void changeInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果输入法打开则关闭，如果没打开则打开
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * 获取输入法状态
     */
    protected boolean getInputState(View view) {
        boolean active = view.hasFocus();
        HhLog.e(active+"");
        return active;
    }
    /**
     * 打开输入法
     */
    protected void openInput(View view) {
        if(!getInputState(view)){
            changeInputState();
        }
    }
    /**
     * 关闭输入法
     */
    protected void closeInput(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 绑定ViewModel
     */
    protected abstract void setupViewModel();

    public abstract int bindLayoutId();

    public abstract V obtainViewModel();
}
