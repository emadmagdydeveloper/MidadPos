package com.midad_app_pos.model;

import java.io.Serializable;

public class BusinessTypeModel implements Serializable {
    private String title_ar;
    private String title_en;
    private boolean isSelected;

    public BusinessTypeModel(String title_ar, String title_en) {
        this.title_ar = title_ar;
        this.title_en = title_en;
        this.isSelected = false;
    }

    public String getTitle_ar() {
        return title_ar;
    }

    public String getTitle_en() {
        return title_en;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
