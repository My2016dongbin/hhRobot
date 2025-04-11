package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.Picture;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class PictureListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<Picture> pictureList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    public void postPictureList(){
        pictureList = new ArrayList<>();
        pictureList.add(new Picture("1","1.png","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",state,false));
        pictureList.add(new Picture("2","2.png","http://112.6.162.92:8000/group1/M00/60/65/CgoCG2fvfImAWkNuAAfbJPvAeSA137.jpg",state,false));
        pictureList.add(new Picture("3","3.png","http://112.6.162.92:8000/group1/M00/60/65/CgoCG2fvfImAZKwSAAE61I1Cyao644.jpg",state,false));
        pictureList.add(new Picture("4","4.png","http://web.ehaohai.com:2018/SatelliteData/H9-FIR/china/2025-04-08/Fire_Result/16/H9-FIR_2025-04-08_0800__16_321_fp.jpg",state,false));
        pictureList.add(new Picture("5","5.png","http://112.6.162.92:8000/group1/M00/60/65/CgoCG2fvfImAWkNuAAfbJPvAeSA137.jpg",state,false));
        pictureList.add(new Picture("6","6.png","http://112.6.162.92:8000/group1/M00/60/65/CgoCG2fvfImAZKwSAAE61I1Cyao644.jpg",state,false));

        updateData();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        items.clear();
        if (pictureList != null && pictureList.size()!=0) {
            items.addAll(pictureList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

}
