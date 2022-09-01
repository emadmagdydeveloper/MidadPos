package com.midad_pos.model;

import java.io.Serializable;

public class AppSettingModel implements Serializable {
    private boolean isScan = false;

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }
}
