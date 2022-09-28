package com.midad_pos.model;

import java.io.Serializable;

public class AppSettingModel implements Serializable {
    private boolean isScan = false;
    private int home_layout_type = 1; //1 grid 2 linear

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
}
