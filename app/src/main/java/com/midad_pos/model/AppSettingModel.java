package com.midad_pos.model;

import java.io.Serializable;

public class AppSettingModel implements Serializable {
    private boolean isScan = false;
    private int home_layout_type = 1; //1 grid 2 linear
    private int isShiftOpen;
    private String shift_id;
    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }

    public int getHome_layout_type() {
        return home_layout_type;
    }

    public void setHome_layout_type(int home_layout_type) {
        this.home_layout_type = home_layout_type;
    }

    public int getIsShiftOpen() {
        return isShiftOpen;
    }

    public void setIsShiftOpen(int isShiftOpen) {
        this.isShiftOpen = isShiftOpen;
    }

    public String getShift_id() {
        return shift_id;
    }

    public void setShift_id(String shift_id) {
        this.shift_id = shift_id;
    }
}
