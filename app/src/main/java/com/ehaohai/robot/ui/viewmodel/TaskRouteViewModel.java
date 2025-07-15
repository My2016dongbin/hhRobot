package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.ui.multitype.AddRoute;
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.Point;
import com.ehaohai.robot.utils.HhLog;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class TaskRouteViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> picture = new MutableLiveData<>();
    public boolean state = false;
    public List<Point> pointList = new ArrayList<>();
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            for (int i = 0; i < pointList.size(); i++) {
                Point point = pointList.get(i);
                point.setIndex(i);
            }
            items.addAll(pointList);
        }
        items.add(new AddRoute());

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSelected(Picture picture) {
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            items.addAll(pointList);
        }
        items.add(new AddRoute());

        assertAllRegistered(adapter, items);
        try{
            adapter.notifyItemChanged(Integer.parseInt(picture.getId()));
        }catch (Exception e){
            adapter.notifyDataSetChanged();
        }
    }
}
