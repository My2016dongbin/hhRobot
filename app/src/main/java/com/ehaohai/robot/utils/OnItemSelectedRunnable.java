package com.ehaohai.robot.utils;


import com.ehaohai.robot.ui.cell.WheelView;

public final class OnItemSelectedRunnable implements Runnable {
    final WheelView wheelView;
    public int oldIndex = -1;

    public OnItemSelectedRunnable(WheelView wheelview) {
        wheelView = wheelview;
    }

    @Override
    public final void run() {
        if (wheelView.getSelectedPosition() < 0) {
            return;
        }
        if (wheelView.getSelectedPosition() == oldIndex) {
            return;
        }
        oldIndex = wheelView.getSelectedPosition();
        wheelView.onItemSelectedListener.onItemSelected(wheelView.getSelectedPosition(),wheelView.getSelectedItem());
    }
}
