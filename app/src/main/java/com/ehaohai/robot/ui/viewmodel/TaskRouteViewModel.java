package com.ehaohai.robot.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.ui.multitype.Picture;
import com.ehaohai.robot.ui.multitype.Point;

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


    public void postList(){
        pointList = new ArrayList<>();

        pointList.add(new Point("1","张三","",state,false));
        pointList.add(new Point("2","李四","",state,false));
        pointList.add(new Point("3","王五","",state,false));
        pointList.add(new Point("4","赵六","",state,false));
        pointList.add(new Point("5","张张","",state,false));
        pointList.add(new Point("6","李李","",state,false));
        pointList.add(new Point("7","宋宋","",state,false));
        pointList.add(new Point("8","王王","",state,false));
        pointList.add(new Point("9","京东","",state,false));
        updateData();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            items.addAll(pointList);
        }else{
            //items.add(new Empty());
            return;
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSelected(Picture picture) {
        items.clear();
        if (pointList != null && pointList.size()!=0) {
            items.addAll(pointList);
        }else{
            //items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        try{
            adapter.notifyItemChanged(Integer.parseInt(picture.getId()));
        }catch (Exception e){
            adapter.notifyDataSetChanged();
        }
    }
}
