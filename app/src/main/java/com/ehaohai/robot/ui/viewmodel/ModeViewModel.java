package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Warn;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class ModeViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<String> mode = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;

    }

}
