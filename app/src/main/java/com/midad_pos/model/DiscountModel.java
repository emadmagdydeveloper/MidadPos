package com.midad_pos.model;

import java.io.Serializable;

public class DiscountModel implements Serializable {
    private String id;
    private String title;
    private String type;
    private String value;
    private boolean for_all;
    private boolean selected;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isFor_all() {
        return for_all;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
