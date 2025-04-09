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

public class WarnListViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final MutableLiveData<String> warn = new MutableLiveData<>();
    public MultiTypeAdapter aiAdapter;
    public MultiTypeAdapter bugAdapter;
    public List<Object> aiItems = new ArrayList<>();
    public List<Object> bugItems = new ArrayList<>();
    public List<Warn> warnList = new ArrayList<>();
    public boolean isAi = true;

    public void start(Context context) {
        this.context = context;

    }

    public void postWarnList(){
        warnList = new ArrayList<>();
        warnList.add(new Warn(0,"0","2024.09.28","机器狗","浩海机器狗","烟雾火焰","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",true));
        warnList.add(new Warn(1,"1","2024.09.28","机器狗","浩海机器狗","灯光状态","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",true));
        warnList.add(new Warn(2,"2","2024.09.28","机器狗","浩海机器狗","灯光状态","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
        warnList.add(new Warn(3,"3","2024.09.28","机器狗","浩海机器狗","人员身份","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
        warnList.add(new Warn(4,"4","2024.09.28","机器狗","浩海机器狗","抽烟识别","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
        warnList.add(new Warn(5,"5","2024.09.28","机器狗","浩海机器狗","烟雾火焰","烟雾火焰识别","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",false));
        updateDataAi();
        updateDataBug();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataAi() {
        aiItems.clear();
        if (warnList != null && warnList.size()!=0) {
            aiItems.addAll(warnList);
        }else{
            aiItems.add(new Empty());
        }

        assertAllRegistered(aiAdapter, aiItems);
        aiAdapter.notifyDataSetChanged();
    }

    public void updateDataBug() {
        bugItems.clear();
        if (warnList != null && warnList.size()!=0) {
            bugItems.addAll(warnList);
        }else{
            bugItems.add(new Empty());
        }

        assertAllRegistered(aiAdapter, bugItems);
        bugAdapter.notifyDataSetChanged();
    }

}
