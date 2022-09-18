package com.midad_pos.model;

import java.io.Serializable;

public class TaxModel implements Serializable {
    private String id;
    private String user_id;
    private String name;
    private String rate;
    private String is_active;
    private String created_at;
    private String updated_at;
    private boolean isChecked;

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getRate() {
        return rate;
    }

    public String getIs_active() {
        return is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
